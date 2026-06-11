'use strict';

let stompClient = null;
let myName = null;
let currentQuestionId = null;
let myAnswer = null;

const joinSection = document.getElementById('join-section');
const waitingSection = document.getElementById('waiting-section');
const questionSection = document.getElementById('question-section');
const joinError = document.getElementById('join-error');
const answerButtons = document.getElementById('answer-buttons');
const answerFeedback = document.getElementById('answer-feedback');

document.getElementById('join-button').addEventListener('click', joinGame);
document.getElementById('player-name').addEventListener('keydown', e => {
    if (e.key === 'Enter') joinGame();
});

function joinGame() {
    const name = document.getElementById('player-name').value.trim();
    if (!name) {
        showJoinError('Bitte gib einen Namen ein.');
        return;
    }
    myName = name;

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, () => {
        stompClient.subscribe('/topic/players', frame => updatePlayerList(JSON.parse(frame.body)));
        stompClient.subscribe('/topic/question', frame => showQuestion(JSON.parse(frame.body)));
        stompClient.subscribe('/topic/answer-result', frame => onAnswerResult(JSON.parse(frame.body)));
        stompClient.subscribe('/topic/question-closed', frame => onQuestionClosed(JSON.parse(frame.body)));

        stompClient.send('/app/join', {}, JSON.stringify({ playerName: myName }));

        document.getElementById('joined-name').textContent = myName;
        showSection(waitingSection);
    }, () => {
        showJoinError('Verbindung fehlgeschlagen. Bitte erneut versuchen.');
    });
}

function showJoinError(message) {
    joinError.textContent = message;
    joinError.hidden = false;
}

function showSection(section) {
    [joinSection, waitingSection, questionSection].forEach(s => s.hidden = (s !== section));
}

function updatePlayerList(broadcast) {
    const list = document.getElementById('player-list');
    list.innerHTML = '';
    broadcast.players.forEach(player => {
        const li = document.createElement('li');
        li.textContent = player.name + ' — ' + player.score + ' Punkte';
        list.appendChild(li);
        if (player.name === myName) {
            document.getElementById('my-score').textContent = player.score;
        }
    });
}

function showQuestion(question) {
    currentQuestionId = question.questionId;
    myAnswer = null;

    document.getElementById('question-points').textContent = question.scoreValue + ' Punkte';
    document.getElementById('question-text').textContent = question.questionText;
    answerFeedback.hidden = true;
    answerFeedback.textContent = '';

    answerButtons.innerHTML = '';
    question.answers.forEach(answer => {
        const button = document.createElement('button');
        button.textContent = answer;
        button.addEventListener('click', () => sendAnswer(answer, button));
        answerButtons.appendChild(button);
    });

    showSection(questionSection);
}

function sendAnswer(answer, clickedButton) {
    if (myAnswer !== null) return;
    myAnswer = answer;

    stompClient.send('/app/answer', {}, JSON.stringify({
        questionId: currentQuestionId,
        selectedAnswer: answer,
        playerName: myName
    }));

    clickedButton.classList.add('selected');
    answerButtons.querySelectorAll('button').forEach(b => b.disabled = true);

    answerFeedback.textContent = 'Antwort abgeschickt – warte auf Auflösung…';
    answerFeedback.hidden = false;
}

function onAnswerResult(result) {
    if (result.playerName !== myName) return;
    document.getElementById('my-score').textContent = result.newScore;
}

function onQuestionClosed(closed) {
    if (closed.questionId !== currentQuestionId) return;

    answerButtons.querySelectorAll('button').forEach(button => {
        button.disabled = true;
        if (button.textContent === closed.correctAnswer) {
            button.classList.add('correct');
        } else if (button.textContent === myAnswer) {
            button.classList.add('wrong');
        }
    });

    if (myAnswer === null) {
        answerFeedback.textContent = 'Keine Antwort abgegeben.';
    } else if (myAnswer === closed.correctAnswer) {
        answerFeedback.textContent = '✓ Richtig!';
    } else {
        answerFeedback.textContent = '✗ Leider falsch.';
    }
    answerFeedback.hidden = false;

    setTimeout(() => showSection(waitingSection), 4000);
}
