console.log("JS loaded");

const TOTAL_QUESTIONS = 10;
let currentQuestion = 0;
let score = 0;


// START QUIZ

async function startQuiz() {

    currentQuestion = 0;
    score = 0;
    document.getElementById("progress-container").style.display = "none";

    const genre = document.getElementById("genre").value;

    if (!genre) {
        alert("Vælg en genre først!");
        return;
    }

    await fetchQuestion(genre);
}

// NÆSTE SPØRGSMÅL

async function nextQuestion() {
    const genre = document.getElementById("genre").value;
    await fetchQuestion(genre);
}

// FETCH

async function fetchQuestion(genre) {
    try {
        const response = await fetch("http://localhost:8080/api/quiz", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({genre : genre})
        });

        if (!response.ok) {
            console.error("Server fejl:", response.status);
            alert("Noget gik galt – prøv igen.");
            return;
        }

        const data = await response.json();

        if (!data || !data.options) {
            console.error("Ugyldigt quiz data:", data);
            return;
        }

        renderQuiz(data);

    } catch (error) {
        console.error("Fejl ved hentning af quiz:", error);
        alert("Kunne ikke forbinde til serveren.");
    }
}

// PROGRESS

function updateProgress() {
    currentQuestion++;
    document.getElementById("progress-container").style.display = "block";
    document.getElementById("progress-text").textContent =
        `Spørgsmål ${currentQuestion} af ${TOTAL_QUESTIONS}`;
    document.getElementById("progress-bar").style.width =
        `${(currentQuestion / TOTAL_QUESTIONS) * 100}%`;
}

// RENDER

function renderQuiz(data) {

    updateProgress();

    const container = document.getElementById("quiz");
    container.innerHTML = "";

    container.innerHTML += `<h2>Hvad hedder sangen?</h2>`;

    if (data.preview) {
        container.innerHTML += `
            <audio controls autoplay>
                <source src="${data.preview}" type="audio/mpeg">
            </audio>
            <br><br>
        `;
    }

    const options = data.options ?? [];

    options.forEach(option => {
        const btn = document.createElement("button");
        btn.textContent = option;

        btn.onclick = () => {
            container.querySelectorAll("button").forEach(b => b.disabled = true);
            checkAnswer(option, data.correctAnswer, data.trackId, data.artistName, data.albumCover);
        };

        container.appendChild(btn);
    });

    // Tilføjer hint-knap efter audio elementet
    const hintBtn = document.createElement("button");
    hintBtn.textContent = "Hint 💡";
    hintBtn.style.background = "#7c3aed";
    hintBtn.onclick = async () => {
        hintBtn.disabled = true;
        hintBtn.textContent = "Henter hint...";

        try {
            const res = await fetch("http://localhost:8080/api/quiz/hint", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    trackName: data.correctAnswer,
                    artistName: data.artistName
                })
            });

            if (res.ok) {
                const hint = await res.text();
                document.getElementById("hint-box").textContent = "💡 " + hint;
            }
        } catch (err) {
            console.error("Kunne ikke hente hint:", err);
        }
    };
    container.appendChild(hintBtn);


    const hintBox = document.createElement("p");
    hintBox.id = "hint-box";
    hintBox.style.color = "#fbbf24";
    hintBtn.style.maxWidth = "300px";
    hintBtn.style.margin = "0 auto";
    container.appendChild(hintBox);

}

// CHECK ANSWER

async function checkAnswer(selected, correct, trackId, artistName, albumCover) {

    if (!correct) {
        alert("Svar-data mangler – prøv et nyt spørgsmål.");
        nextQuestion();
        return;
    }

    const quiz = document.getElementById("quiz");

    if (selected.toLowerCase() === correct.toLowerCase()) {
        score++;
        quiz.innerHTML += `<p>✅ Rigtigt! Det var: <b>${correct}</b> af <b>${artistName}</b></p>`;
    } else {
        quiz.innerHTML += `<p>❌ Forkert! Det var: <b>${correct}</b> af <b>${artistName}</b></p>`;
    }

    // Fun fact

    if (artistName) {
        try {
            const res = await fetch("http://localhost:8080/api/quiz/funfact", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(artistName)
            });

            if (res.ok) {
                const fact = await res.text();
                quiz.innerHTML += `<p><b>🎤 Fun fact:</b> ${fact}</p>`;
            }
        } catch (err) {
            console.error("Kunne ikke hente fun fact:", err);
        }
    }

    // Vis album cover efter svar
    if (albumCover) {
        quiz.innerHTML += `
            <img src="${albumCover}" 
                 alt="Album cover"
                 style="width:200px; height:200px; border-radius:12px; margin:15px auto; display:block;">
        `;
    }

    if (currentQuestion >= TOTAL_QUESTIONS) {
        const doneBtn = document.createElement("button");
        doneBtn.textContent = "Se resultat 🏆";
        doneBtn.onclick = () => showResult();
        quiz.appendChild(doneBtn);
    } else {
        const nextBtn = document.createElement("button");
        nextBtn.textContent = "Næste spørgsmål ➡️";
        nextBtn.onclick = () => nextQuestion();
        quiz.appendChild(nextBtn);
    }
}

//   RESULTAT

function showResult() {
    document.getElementById("progress-container").style.display = "none";

    const quiz = document.getElementById("quiz");

    let emoji = "😢";
    if (score >= 5) emoji = "👍";
    if (score >= 8) emoji = "🏆";

    quiz.innerHTML = `
        <h2>Quiz slut! ${emoji}</h2>
        <p style="font-size: 2rem; font-weight: bold; color: #93c5fd;">
            ${score} / ${TOTAL_QUESTIONS}
        </p>
        <p>${getResultText(score)}</p>
    `;
}

function getResultText(score) {
    if (score === TOTAL_QUESTIONS) return "Perfekt! Du er en ægte musiknørd! 🎵";
    if (score >= 8) return "Imponerende! Du kender din musik!";
    if (score >= 5) return "Ikke dårligt! Øvelse gør mester.";
    if (score >= 3) return "Bedre held næste gang!";
    return "Måske ikke din genre? Prøv en anden! 😅";
}