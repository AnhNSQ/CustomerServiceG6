// Customer Authentication JavaScript

// Global variables
let currentForm = 'login';

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeAuth();
    setupEventListeners();
});

// Initialize authentication forms
function initializeAuth() {
    // Set initial form
    showForm('login');
    
    // Initialize form validation
    initializeValidation();
}

// Setup event listeners
function setupEventListeners() {
    // Form submission handlers
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const forgotForm = document.getElementById('forgot-form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
    
    if (forgotForm) {
        forgotForm.addEventListener('submit', handleForgotPassword);
    }
    
    // Real-time validation
    document.querySelectorAll('.form-control').forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            if (this.classList.contains('is-invalid')) {
                validateField(this);
            }
        });
    });
}

// Form navigation functions
function showForm(formType) {
    hideAllForms();
    currentForm = formType;
    
    const formElement = document.getElementById(formType + 'Form');
    if (formElement) {
        formElement.classList.add('active');
    }
}

function hideAllForms() {
    document.querySelectorAll('.form-step').forEach(form => {
        form.classList.remove('active');
    });
}

// Show login form
function showLoginForm() {
    showForm('login');
}

// Show register form
function showRegisterForm() {
    showForm('register');
}

// Show forgot password form
function showForgotPassword() {
    showForm('forgot');
}

// Password toggle function
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = input.parentElement.querySelector('.password-toggle i');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'fas fa-eye-slash';
    } else {
        input.type = 'password';
        icon.className = 'fas fa-eye';
    }
}

// Handle login form submission
function handleLogin(e) {
    e.preventDefault();
    
    const form = e.target;
    const formData = new FormData(form);
    const email = formData.get('email');
    const password = formData.get('password');
    const rememberMe = formData.get('rememberMe');
    
    // Validate form
    if (!validateLoginForm(email, password)) {
        return;
    }
    
    // Show loading state
    const button = form.querySelector('button[type="submit"]');
    showLoadingState(button);
    
    // Simulate API call
    setTimeout(() => {
        // Here you would make actual API call
        // For now, simulate success
        showAlert('success', 'Đăng nhập thành công! Đang chuyển hướng...');
        
        setTimeout(() => {
            hideLoadingState(button);
            
            // Redirect to customer dashboard
            window.location.href = '/dashboard';
        }, 1000);
    }, 2000);
}

// Handle register form submission
function handleRegister(e) {
    e.preventDefault();
    
    const form = e.target;
    const formData = new FormData(form);
    const name = formData.get('name');
    const email = formData.get('email');
    const password = formData.get('password');
    const confirmPassword = formData.get('confirmPassword');
    const agreeTerms = formData.get('agreeTerms');
    
    // Validate form
    if (!validateRegisterForm(name, email, password, confirmPassword, agreeTerms)) {
        return;
    }
    
    // Show loading state
    const button = form.querySelector('button[type="submit"]');
    showLoadingState(button);
    
    // Simulate API call
    setTimeout(() => {
        // Here you would make actual API call
        // For now, simulate success
        showAlert('success', 'Tài khoản đã được tạo thành công!');
        
        setTimeout(() => {
            hideLoadingState(button);
            
            // Switch to login form
            showLoginForm();
        }, 1000);
    }, 2000);
}

// Handle forgot password form submission
function handleForgotPassword(e) {
    e.preventDefault();
    
    const form = e.target;
    const formData = new FormData(form);
    const email = formData.get('email');
    
    // Validate email
    if (!validateEmail(email)) {
        showAlert('danger', 'Vui lòng nhập email hợp lệ!');
        return;
    }
    
    // Show loading state
    const button = form.querySelector('button[type="submit"]');
    showLoadingState(button);
    
    // Simulate API call
    setTimeout(() => {
        // Here you would make actual API call
        // For now, simulate success
        showAlert('success', 'Link khôi phục mật khẩu đã được gửi đến email của bạn!');
        
        setTimeout(() => {
            hideLoadingState(button);
        }, 1000);
    }, 2000);
}

// Google authentication functions
function loginWithGoogle() {
    showAlert('info', 'Đang chuyển hướng đến Google...');
    
    // Here you would integrate with Google OAuth
    setTimeout(() => {
        showAlert('success', 'Đăng nhập Google thành công!');
        // Redirect to dashboard
        window.location.href = '/dashboard';
    }, 1500);
}

function registerWithGoogle() {
    showAlert('info', 'Đang chuyển hướng đến Google...');
    
    // Here you would integrate with Google OAuth
    setTimeout(() => {
        showAlert('success', 'Đăng ký Google thành công!');
        // Redirect to dashboard
        window.location.href = '/dashboard';
    }, 1500);
}

// Form validation functions
function validateLoginForm(email, password) {
    let isValid = true;
    
    if (!validateEmail(email)) {
        showAlert('danger', 'Vui lòng nhập email hợp lệ!');
        isValid = false;
    }
    
    if (!password || password.length < 6) {
        showAlert('danger', 'Mật khẩu phải có ít nhất 6 ký tự!');
        isValid = false;
    }
    
    return isValid;
}

function validateRegisterForm(name, email, password, confirmPassword, agreeTerms) {
    let isValid = true;
    
    if (!name || name.trim().length < 2) {
        showAlert('danger', 'Họ và tên phải có ít nhất 2 ký tự!');
        isValid = false;
    }
    
    if (!validateEmail(email)) {
        showAlert('danger', 'Vui lòng nhập email hợp lệ!');
        isValid = false;
    }
    
    if (!password || password.length < 6) {
        showAlert('danger', 'Mật khẩu phải có ít nhất 6 ký tự!');
        isValid = false;
    }
    
    if (password !== confirmPassword) {
        showAlert('danger', 'Mật khẩu xác nhận không khớp!');
        isValid = false;
    }
    
    if (!agreeTerms) {
        showAlert('danger', 'Vui lòng đồng ý với điều khoản sử dụng!');
        isValid = false;
    }
    
    return isValid;
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function validateField(field) {
    const value = field.value.trim();
    let isValid = true;
    let message = '';

    // Remove existing validation classes
    field.classList.remove('is-valid', 'is-invalid');

    switch (field.type) {
        case 'email':
            isValid = validateEmail(value);
            message = 'Email không hợp lệ';
            break;
        case 'password':
            if (field.id === 'registerPassword') {
                isValid = value.length >= 6;
                message = 'Mật khẩu phải có ít nhất 6 ký tự';
            } else if (field.id === 'confirmPassword') {
                const password = document.getElementById('registerPassword').value;
                isValid = value === password;
                message = 'Mật khẩu xác nhận không khớp';
            }
            break;
        default:
            isValid = value.length > 0;
            message = 'Trường này không được để trống';
    }

    if (value.length > 0) {
        field.classList.add(isValid ? 'is-valid' : 'is-invalid');
        
        // Add/remove invalid feedback
        let feedback = field.parentElement.querySelector('.invalid-feedback');
        if (!isValid) {
            if (!feedback) {
                feedback = document.createElement('div');
                feedback.className = 'invalid-feedback';
                field.parentElement.appendChild(feedback);
            }
            feedback.textContent = message;
        } else if (feedback) {
            feedback.remove();
        }
    }
}

// Initialize form validation
function initializeValidation() {
    // Add validation attributes to form inputs
    const emailInputs = document.querySelectorAll('input[type="email"]');
    emailInputs.forEach(input => {
        input.setAttribute('pattern', '[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$');
    });
    
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    passwordInputs.forEach(input => {
        input.setAttribute('minlength', '6');
    });
}

// Loading state functions
function showLoadingState(button) {
    const btnText = button.querySelector('.btn-text');
    const loading = button.querySelector('.loading');
    
    if (btnText) btnText.style.opacity = '0';
    if (loading) loading.classList.add('active');
    
    button.disabled = true;
}

function hideLoadingState(button) {
    const btnText = button.querySelector('.btn-text');
    const loading = button.querySelector('.loading');
    
    if (btnText) btnText.style.opacity = '1';
    if (loading) loading.classList.remove('active');
    
    button.disabled = false;
}

// Alert function
function showAlert(type, message) {
    // Remove existing alerts
    const existingAlert = document.querySelector('.alert');
    if (existingAlert) {
        existingAlert.remove();
    }
    
    const alert = document.createElement('div');
    alert.className = `alert alert-${type === 'info' ? 'primary' : type} alert-dismissible fade show`;
    
    const iconClass = getAlertIcon(type);
    alert.innerHTML = `
        <i class="fas fa-${iconClass} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    // Insert at the beginning of active form
    const activeForm = document.querySelector('.form-step.active');
    if (activeForm) {
        activeForm.insertBefore(alert, activeForm.firstChild);
    }
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (alert && alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}

function getAlertIcon(type) {
    switch (type) {
        case 'success': return 'check-circle';
        case 'danger': return 'exclamation-circle';
        case 'warning': return 'exclamation-triangle';
        case 'info': return 'info-circle';
        default: return 'info-circle';
    }
}

// Utility functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Export functions for global access
window.showLoginForm = showLoginForm;
window.showRegisterForm = showRegisterForm;
window.showForgotPassword = showForgotPassword;
window.togglePassword = togglePassword;
window.loginWithGoogle = loginWithGoogle;
window.registerWithGoogle = registerWithGoogle;
