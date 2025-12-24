function toggleLike(postId, isCurrentlyLiked) {

    const likeButton = event.target.closest('.like-btn');

    const actualPostId = likeButton.getAttribute('data-post-id') || postId;
    const actualIsLiked = likeButton.getAttribute('data-liked') === 'true';

    const likesCountElement = likeButton.querySelector('.likes-count');

    const url = `${contextPath}/like?postId=${actualPostId}`;

    const method = actualIsLiked ? 'DELETE' : 'POST';

    likeButton.disabled = true;

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {

            if (response.ok) {
                return response.json();
            } else {
                if (response.status === 400) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.error || 'Already liked/not liked');
                    });
                }
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
        })
        .then(data => {
            if (data.success) {
                likesCountElement.textContent = data.likes;

                if (data.isLiked) {
                    likeButton.classList.add('liked');
                    likeButton.querySelector('i').className = 'bi bi-hand-thumbs-up-fill';
                    likeButton.setAttribute('data-liked', 'true');
                } else {
                    likeButton.classList.remove('liked');
                    likeButton.querySelector('i').className = 'bi bi-hand-thumbs-up';
                    likeButton.setAttribute('data-liked', 'false');
                }
            }
        })
        .catch(error => {
            console.error('Fetch Error:', error);
            if (error.message.includes('HTTP 401')) {
                window.location.href = `${contextPath}/login?redirect=${encodeURIComponent(window.location.href)}`;
            }
        })
        .finally(() => {
            likeButton.disabled = false;
        });
}