const form = document.getElementById('uploadForm');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const file = document.getElementById('fileInput').files[0];
    if (!file) return;

    // Show uploaded image
    const reader = new FileReader();
    reader.onload = () => {
        document.getElementById('uploadedImage').innerHTML = `<img src="${reader.result}" alt="Uploaded Image">`;
    };
    reader.readAsDataURL(file);

    // Upload image to backend
    const formData = new FormData();
    formData.append('file', file);

    try {
        const res = await fetch('http://localhost:8080/api/products/match', {
            method: 'POST',
            body: formData
        });

        const data = await res.json();
        const resultsDiv = document.getElementById('results');
        resultsDiv.innerHTML = '';

        if (data.length === 0) {
            resultsDiv.innerHTML = '<p style="grid-column:1/-1; text-align:center;">No similar products found.</p>';
        } else {
            data.forEach(product => {
                resultsDiv.innerHTML += `
                    <div class="product-card">
                        <img src="http://localhost:8080/api/products/image/${product.imageUrl}" alt="${product.name}">
                        <p>${product.name}</p>
                        <p>${product.category}</p>
                        <p class="similarity">Similarity: ${product.similarityScore.toFixed(2)}%</p>
                    </div>
                `;
            });
        }
    } catch (err) {
        console.error(err);
        alert("Error finding similar products.");
    }
});
