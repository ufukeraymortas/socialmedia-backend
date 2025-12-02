document.addEventListener('DOMContentLoaded', () => {
    
    // --- AYARLAR ---
    // Render Backend adresiniz
    const API_URL = 'https://senato.onrender.com'; 

    // --- ELEMENTLERİ GÜVENLİ SEÇME FONKSİYONU ---
    const getEl = (id) => document.getElementById(id);

    // Konteynerler
    const authContainer = getEl('auth-container');
    const mainAppContainer = getEl('main-app-container');
    const feedView = getEl('feed-view');
    const profileView = getEl('profile-view');

    // Formlar
    const loginForm = getEl('login-form');
    const registerForm = getEl('register-form');
    const postForm = getEl('post-form');
    
    // Listeler
    const postsContainer = getEl('posts-container');
    const profileContentContainer = getEl('profile-content-container');

    // Profil Sayfası Elementleri
    const profileUsernameEl = getEl('profile-username');
    const profileTitleEl = getEl('profile-title');
    const profileBioEl = getEl('profile-bio');
    const profilePictureEl = getEl('profile-picture');
    
    const editProfileBtn = getEl('edit-profile-btn');
    const editProfileForm = getEl('edit-profile-form');
    const cancelEditBtn = getEl('cancel-edit-btn');
    
    const backToFeedBtn = getEl('back-to-feed-btn');
    const myProfileBtn = getEl('my-profile-btn');
    const logoutBtn = getEl('logout-btn');

    // Durum Değişkenleri
    let currentUserId = null;
    let currentUsername = null;

    // ---------------------------------------------------------
    // 1. TEMEL FONKSİYONLAR
    // ---------------------------------------------------------

    function showApp(user) {
        currentUserId = user.id;
        currentUsername = user.username;
        
        // Hoşgeldin mesajı (Opsiyonel)
        const currentUserInfo = getEl('current-user-info'); // Eğer HTML'de varsa
        if(currentUserInfo) currentUserInfo.textContent = `Hoş geldin, ${currentUsername}!`;

        if(authContainer) authContainer.classList.add('hidden');
        if(mainAppContainer) mainAppContainer.classList.remove('hidden');
        
        // Varsayılan olarak ana akışı göster
        showFeed();
    }

    function showFeed() {
        if(profileView) profileView.classList.add('hidden');
        if(feedView) feedView.classList.remove('hidden');
        loadGlobalPosts(); 
    }

    // Global Fonksiyon: Profili Aç
    window.openProfile = async function(userId) {
        if(feedView) feedView.classList.add('hidden');
        if(profileView) profileView.classList.remove('hidden');
        
        await loadUserProfileHeader(userId);
        await loadUserPosts(userId);

        // Kendi profilim mi?
        if (editProfileBtn) {
            if (userId === currentUserId) {
                editProfileBtn.classList.remove('hidden');
            } else {
                editProfileBtn.classList.add('hidden');
                if(editProfileForm) editProfileForm.classList.add('hidden');
            }
        }
    };

    function showAuth() {
        currentUserId = null;
        currentUsername = null;
        if(mainAppContainer) mainAppContainer.classList.add('hidden');
        if(authContainer) authContainer.classList.remove('hidden');
        if(loginForm) loginForm.reset();
    }

    // ---------------------------------------------------------
    // 2. VERİ ÇEKME (FETCH)
    // ---------------------------------------------------------

    // A. Global Akış
    async function loadGlobalPosts() {
        if(!postsContainer) return;
        postsContainer.innerHTML = '<p style="text-align:center;">Yükleniyor...</p>';
        try {
            const res = await fetch(`${API_URL}/api/posts`);
            const posts = await res.json();
            renderPosts(posts, postsContainer);
        } catch (error) {
            console.error(error);
        }
    }

    // B. Kişi Profili Postları
    async function loadUserPosts(userId) {
        if(!profileContentContainer) return;
        profileContentContainer.innerHTML = '<p style="text-align:center;">Yükleniyor...</p>';
        try {
            const res = await fetch(`${API_URL}/api/posts/user/${userId}`);
            const posts = await res.json();
            
            if (posts.length === 0) {
                profileContentContainer.innerHTML = '<p style="text-align:center; margin-top:20px;">Henüz gönderi yok.</p>';
            } else {
                renderPosts(posts, profileContentContainer);
            }
        } catch (error) {
            console.error(error);
        }
    }

    // C. Profil Başlık Bilgileri
    async function loadUserProfileHeader(userId) {
        try {
            const res = await fetch(`${API_URL}/api/users/${userId}`);
            const user = await res.json();

            if(profileUsernameEl) profileUsernameEl.textContent = user.username;
            if(profileTitleEl) profileTitleEl.textContent = user.title || 'Ünvan Yok';
            if(profileBioEl) profileBioEl.textContent = user.bio || 'Biyografi yok.';
            
            if(profilePictureEl) {
                if (user.profilePictureUrl) {
                    profilePictureEl.src = `${API_URL}/uploads/${user.profilePictureUrl}`;
                } else {
                    // GÜVENİLİR VARSAYILAN RESİM
                    profilePictureEl.src = 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png';
                }
            }

            // Formu doldur (Kendi profilimse)
            if (userId === currentUserId) {
                const titleInput = getEl('edit-title-input');
                const bioInput = getEl('edit-bio-input');
                if(titleInput) titleInput.value = user.title || '';
                if(bioInput) bioInput.value = user.bio || '';
            }

        } catch (error) {
            console.error('Profil bilgisi alınamadı:', error);
        }
    }

    // ---------------------------------------------------------
    // 3. ORTAK HTML OLUŞTURUCU
    // ---------------------------------------------------------
    
    async function renderPosts(posts, container) {
        container.innerHTML = '';

        for (const post of posts) {
            const postEl = document.createElement('div');
            postEl.className = 'post';
            
            // Medya
            let mediaHtml = '';
            if (post.imageUrl) mediaHtml = `<img src="${API_URL}/uploads/${post.imageUrl}" class="post-image">`;
            else if (post.videoUrl) mediaHtml = `<video src="${API_URL}/uploads/${post.videoUrl}" class="post-video" controls></video>`;

            // Beğeni Durumu
            const likeInfo = await checkLikeStatus(post.id);

            postEl.innerHTML = `
                <div class="post-header" style="cursor:pointer;" onclick="openProfile(${post.author.id})">
                    <strong style="color:#0563bb; font-size:16px;">${post.author.username}</strong> 
                    <small style="color:#888; margin-left:5px;">${new Date(post.timestamp).toLocaleString()}</small>
                </div>
                
                <div class="post-content" style="margin-top:5px;">${post.content}</div>
                ${mediaHtml}
                
                <div class="post-actions">
                    <button class="like-btn ${likeInfo.isLiked ? 'liked' : ''}" 
                            data-post-id="${post.id}" onclick="event.stopPropagation()">
                        ${likeInfo.isLiked ? '❤️' : '🤍'} <span id="count-${post.id}">${likeInfo.count}</span>
                    </button>
                </div>

                <div class="comments-section">
                    <div class="comments-list" id="comments-list-${post.id}"></div>
                    
                    <div class="comment-form-container" id="comment-form-container-${post.id}">
                        <div class="replying-to-alert hidden" id="reply-alert-${post.id}">
                            <span>Cevap veriliyor...</span>
                            <button onclick="cancelReply(${post.id})">X</button>
                        </div>

                        <form class="comment-form" data-post-id="${post.id}" onclick="event.stopPropagation()">
                            <textarea placeholder="Yorum yap..." required></textarea>
                            <input type="file" accept="image/*,video/*">
                            <button type="submit">Gönder</button>
                        </form>
                    </div>
                </div>
            `;
            
            container.appendChild(postEl);
            
            // Event Listeners
            const likeBtn = postEl.querySelector('.like-btn');
            if(likeBtn) likeBtn.addEventListener('click', (e) => toggleLike(e.currentTarget));
            
            const commentForm = postEl.querySelector('.comment-form');
            if(commentForm) commentForm.addEventListener('submit', (e) => handleCommentSubmit(e));

            loadComments(post.id);
        }
    }

    // ---------------------------------------------------------
    // 4. YARDIMCI API FONKSİYONLARI
    // ---------------------------------------------------------

    async function checkLikeStatus(postId) {
        try {
            if(!currentUserId) return { count: 0, isLiked: false };
            const res = await fetch(`${API_URL}/api/posts/${postId}/likes?userId=${currentUserId}`);
            return await res.json();
        } catch { return { count: 0, isLiked: false }; }
    }

    async function toggleLike(btn) {
        if(!currentUserId) { alert('Giriş yapmalısınız'); return; }
        const postId = btn.dataset.postId;
        const res = await fetch(`${API_URL}/api/posts/${postId}/likes?userId=${currentUserId}`, { method: 'POST' });
        if (res.ok) {
            const newStatus = await checkLikeStatus(postId);
            btn.className = `like-btn ${newStatus.isLiked ? 'liked' : ''}`;
            btn.innerHTML = `${newStatus.isLiked ? '❤️' : '🤍'} <span id="count-${postId}">${newStatus.count}</span>`;
        }
    }

    async function loadComments(postId) {
        const listEl = document.getElementById(`comments-list-${postId}`);
        if(!listEl) return;
        try {
            const res = await fetch(`${API_URL}/api/posts/${postId}/comments`);
            const comments = await res.json();
            listEl.innerHTML = '';
            renderCommentsRecursive(comments, listEl, postId);
        } catch(e) { console.error(e); }
    }

    function renderCommentsRecursive(comments, container, postId) {
        comments.forEach(comment => {
            const commentEl = document.createElement('div');
            commentEl.className = 'comment';
            
            let mediaHtml = '';
            if (comment.imageUrl) mediaHtml = `<img src="${API_URL}/uploads/${comment.imageUrl}" class="comment-image">`;
            else if (comment.videoUrl) mediaHtml = `<video src="${API_URL}/uploads/${comment.videoUrl}" class="comment-video" controls></video>`;
            
            commentEl.innerHTML = `
                <div class="comment-header">${comment.author.username}</div>
                <div class="comment-content">${comment.content}</div>
                ${mediaHtml}
                <button class="reply-btn" onclick="prepareReply(${postId}, ${comment.id}, '${comment.author.username}')">Cevapla</button>
                <div class="replies-container" id="replies-${comment.id}"></div>
            `;
            
            container.appendChild(commentEl);

            if (comment.replies && comment.replies.length > 0) {
                const repliesContainer = document.getElementById(`replies-${comment.id}`);
                renderCommentsRecursive(comment.replies, repliesContainer, postId);
            }
        });
    }

    async function handleCommentSubmit(e) {
        e.preventDefault();
        if(!currentUserId) { alert('Giriş yapmalısınız'); return; }
        
        const form = e.target;
        const postId = form.dataset.postId;
        const content = form.querySelector('textarea').value;
        const fileInput = form.querySelector('input[type="file"]');
        const file = fileInput ? fileInput.files[0] : null;
        const parentId = form.dataset.parentCommentId || null;

        const formData = new FormData();
        formData.append('content', content);
        formData.append('authorId', currentUserId);
        if (file) formData.append('file', file);
        if (parentId) formData.append('parentCommentId', parentId);

        try {
            const res = await fetch(`${API_URL}/api/posts/${postId}/comments`, { method: 'POST', body: formData });
            if(res.ok) {
                form.reset();
                if(fileInput) fileInput.value = null;
                cancelReply(postId);
                loadComments(postId);
            } else {
                alert('Yorum gönderilemedi.');
            }
        } catch(err) { alert('Hata: ' + err); }
    }

    // Global Cevap Fonksiyonları
    window.prepareReply = function(postId, commentId, username) {
        const formContainer = document.getElementById(`comment-form-container-${postId}`);
        if(!formContainer) return;
        const alertBox = document.getElementById(`reply-alert-${postId}`);
        const form = formContainer.querySelector('form');
        
        form.dataset.parentCommentId = commentId;
        alertBox.classList.remove('hidden');
        alertBox.querySelector('span').textContent = `@${username} kişisine cevap veriyorsun`;
        form.scrollIntoView({ behavior: 'smooth' });
    };

    window.cancelReply = function(postId) {
        const formContainer = document.getElementById(`comment-form-container-${postId}`);
        if(!formContainer) return;
        const alertBox = document.getElementById(`reply-alert-${postId}`);
        const form = formContainer.querySelector('form');
        
        delete form.dataset.parentCommentId;
        alertBox.classList.add('hidden');
    };

    // ---------------------------------------------------------
    // 5. BUTON VE FORM OLAYLARI (GÜVENLİ)
    // ---------------------------------------------------------

    // Profil Düzenle Butonu
    if(editProfileBtn) {
        editProfileBtn.addEventListener('click', () => {
            if(editProfileForm) {
                editProfileForm.classList.remove('hidden');
                // Formu doldur
                const tInput = getEl('edit-title-input');
                const bInput = getEl('edit-bio-input');
                if(tInput && profileTitleEl) tInput.value = profileTitleEl.textContent;
                if(bInput && profileBioEl) bInput.value = profileBioEl.textContent;
            }
        });
    }

    // İptal Butonu
    if(cancelEditBtn) {
        cancelEditBtn.addEventListener('click', () => {
            if(editProfileForm) editProfileForm.classList.add('hidden');
        });
    }

    // Profil Güncelleme Formu
    if(editProfileForm) {
        editProfileForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const title = getEl('edit-title-input')?.value;
            const bio = getEl('edit-bio-input')?.value;
            const file = getEl('edit-pp-input')?.files[0];

            const formData = new FormData();
            formData.append('title', title);
            formData.append('bio', bio);
            if (file) formData.append('profilePicture', file);

            try {
                const res = await fetch(`${API_URL}/api/users/${currentUserId}/profile`, {
                    method: 'PUT', body: formData
                });
                if (res.ok) {
                    alert('Profil güncellendi!');
                    editProfileForm.classList.add('hidden');
                    loadUserProfileHeader(currentUserId);
                } else {
                    alert('Güncelleme başarısız.');
                }
            } catch (err) { alert('Hata: ' + err); }
        });
    }

    // Navigasyon
    if(backToFeedBtn) backToFeedBtn.addEventListener('click', showFeed);
    if(myProfileBtn) myProfileBtn.addEventListener('click', () => openProfile(currentUserId));
    if(logoutBtn) logoutBtn.addEventListener('click', showAuth);

    // Post Atma
    if(postForm) {
        postForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            if(!currentUserId) return;
            
            const contentEl = getEl('post-content');
            const fileEl = getEl('post-file');
            
            const formData = new FormData();
            formData.append('content', contentEl.value);
            formData.append('authorId', currentUserId);
            if (fileEl.files[0]) formData.append('file', fileEl.files[0]);

            try {
                const res = await fetch(`${API_URL}/api/posts`, { method: 'POST', body: formData });
                if (res.ok) {
                    postForm.reset();
                    fileEl.value = null;
                    loadGlobalPosts();
                } else {
                    alert('Post gönderilemedi.');
                }
            } catch(err) { alert('Hata: ' + err); }
        });
    }

    // Giriş
    if(loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const u = getEl('login-username').value;
            const p = getEl('login-password').value;
            try {
                const res = await fetch(`${API_URL}/api/users/login`, {
                    method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({username:u, password:p})
                });
                if(res.ok) showApp(await res.json());
                else alert('Giriş Başarısız.');
            } catch(err) { alert('Sunucu hatası: ' + err); }
        });
    }

    // Kayıt
    if(registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const u = getEl('register-username').value;
            const p = getEl('register-password').value;
            try {
                const res = await fetch(`${API_URL}/api/users/register`, {
                    method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({username:u, password:p})
                });
                if(res.ok) { alert('Kayıt Başarılı!'); showApp(await res.json()); }
                else alert('Kayıt Başarısız.');
            } catch(err) { alert('Sunucu hatası: ' + err); }
        });
    }

    const showLoginBtn = getEl('show-login-btn');
    const showRegisterBtn = getEl('show-register-btn');

    if(showLoginBtn) {
        showLoginBtn.onclick = () => { 
            if(loginForm) loginForm.classList.remove('hidden'); 
            if(registerForm) registerForm.classList.add('hidden'); 
        };
    }
    if(showRegisterBtn) {
        showRegisterBtn.onclick = () => { 
            if(loginForm) loginForm.classList.add('hidden'); 
            if(registerForm) registerForm.classList.remove('hidden'); 
        };
    }
});