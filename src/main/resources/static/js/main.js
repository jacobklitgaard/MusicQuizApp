console.log("🔥 JS loaded");

async function startQuiz() {

    const genre = document.getElementById("genre").value;
    const decade = document.getElementById("decade").value;

    try {
        const response = await fetch("http://localhost:8080/api/quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                genre: genre,
                decade: decade
            })
        });

        const data = await response.json();

        renderQuiz(data);

    } catch (error) {
        console.error("Fejl:", error);
    }
}

function renderQuiz(data) {

    const container = document.getElementById("quiz");

    container.innerHTML = `
        <h2>${data.question}</h2>

        <audio controls>
            <source src="${data.preview}" type="audio/mpeg">
        </audio>

        <br><br>

        ${data.options.map(option => `
            <button onclick="checkAnswer('${option}', '${data.correctAnswer}', '${data.trackId}')">
                ${option}
            </button>
        `).join("")}
    `;
}

async function checkAnswer(selected, correct, trackId) {

    if (selected.toLowerCase() === correct.toLowerCase()) {
        alert("✅ Rigtigt!");

        // BONUS spørgsmål
        const response = await fetch("http://localhost:8080/api/quiz/bonus", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                trackId: trackId
            })
        });

        const data = await response.json();

        renderQuiz(data);

    } else {
        alert("❌ Forkert! Rigtigt svar: " + correct);
    }
}