console.log("🔥 JS loaded");

/* =========================
   START QUIZ
========================= */
async function startQuiz() {

    const genre = document.getElementById("genre").value;

    if (!genre) {
        alert("Vælg en genre først!");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/quiz", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ genre })
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

/* =========================
   RENDER
========================= */
function renderQuiz(data) {

    const container = document.getElementById("quiz");
    container.innerHTML = "";

    container.innerHTML += `<h2>${data.question ?? "Spørgsmål mangler"}</h2>`;

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
            checkAnswer(option, data.correctAnswer, data.trackId, data.artistName);
        };

        container.appendChild(btn);
    });
}

/* =========================
   CHECK ANSWER
========================= */
async function checkAnswer(selected, correct, trackId, artistName) {

    if (!correct) {
        alert("Svar-data mangler – prøv et nyt spørgsmål.");
        startQuiz();
        return;
    }

    const quiz = document.getElementById("quiz");

    if (selected.toLowerCase() === correct.toLowerCase()) {

        quiz.innerHTML += `<p>✅ Rigtigt!</p>`;

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

    } else {
        quiz.innerHTML += `<p>❌ Forkert! Det var: <b>${correct}</b></p>`;
    }

    const nextBtn = document.createElement("button");
    nextBtn.textContent = "Næste spørgsmål ➡️";
    nextBtn.onclick = () => startQuiz();
    quiz.appendChild(nextBtn);
}