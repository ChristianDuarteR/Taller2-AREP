document.getElementById('fetchDataBtn').addEventListener('click', function() {
    fetch('/api/data')
        .then(response => response.json())
        .then(data => {
            document.getElementById('dataOutput').textContent = JSON.stringify(data, null, 2);
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('addMessageBtn').addEventListener('click', function() {
    const message = document.getElementById('newMessage').value;

    fetch('/api/data', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            message: message
        })
    })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
            // Actualiza los datos después de agregar el mensaje
            document.getElementById('dataOutput').textContent = JSON.stringify(data, null, 2);
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('deleteMessageBtn').addEventListener('click', function() {
    const id = document.getElementById('deleteId').value;

    fetch(`/api/data/${id}`, {
        method: 'DELETE'
    })
        .then(response => response.json())
        .then(data => {
            console.log('Deleted:', data);
            // Actualiza los datos después de eliminar el mensaje
        })
        .catch(error => console.error('Error:', error));
});


