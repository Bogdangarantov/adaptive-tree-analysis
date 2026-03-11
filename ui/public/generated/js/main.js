// Main JavaScript - Animations and Interactions

// Scroll to section function
function scrollToSection(sectionId) {
    const section = document.getElementById(sectionId);
    if (section) {
        section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

// Intersection Observer for fade-in animations
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('visible');
        }
    });
}, observerOptions);

function initFadeInObserver() {
    const fadeElements = document.querySelectorAll('.fade-in');
    fadeElements.forEach(el => observer.observe(el));
}

// Observe all fade-in elements
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initFadeInObserver);
} else {
    initFadeInObserver();
}

// Navbar scroll effect
let lastScroll = 0;
const navbar = document.querySelector('.navbar');

window.addEventListener('scroll', () => {
    const currentScroll = window.pageYOffset;
    
    if (currentScroll > 100) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
    
    lastScroll = currentScroll;
});

// Smooth scroll for navigation links
document.querySelectorAll('.nav-menu a').forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const targetId = link.getAttribute('href').substring(1);
        scrollToSection(targetId);
    });
});

// Parallax effect for hero section
window.addEventListener('scroll', () => {
    const scrolled = window.pageYOffset;
    const hero = document.querySelector('.hero');
    
    if (hero && scrolled < window.innerHeight) {
        hero.style.transform = `translateY(${scrolled * 0.5}px)`;
        hero.style.opacity = 1 - (scrolled / window.innerHeight);
    }
});

// Loading animation
window.addEventListener('load', () => {
    document.body.style.opacity = '0';
    
    setTimeout(() => {
        document.body.style.transition = 'opacity 0.5s ease';
        document.body.style.opacity = '1';
    }, 100);
});

// Add hover effect to cards
document.querySelectorAll('.glass-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-8px) scale(1.02)';
    });
    
    card.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0) scale(1)';
    });
});

// Mobile menu toggle (for future mobile navigation)
const createMobileMenu = () => {
    const navMenu = document.querySelector('.nav-menu');
    
    if (window.innerWidth <= 768) {
        navMenu.style.display = 'none';
    } else {
        navMenu.style.display = 'flex';
    }
};

window.addEventListener('resize', createMobileMenu);
createMobileMenu();

// Console welcome message
console.log('%c🌳 Adaptive Tree Analysis', 'color: #00d4ff; font-size: 20px; font-weight: bold;');
console.log('%cExploring Self-Balancing Data Structures', 'color: #10b981; font-size: 14px;');
console.log('%cBuilt with ❤️ for Data Structures & Algorithms', 'color: #7c3aed; font-size: 12px;');

// Performance metrics logging (for development)
if (window.performance) {
    window.addEventListener('load', () => {
        setTimeout(() => {
            const perfData = window.performance.timing;
            const pageLoadTime = perfData.loadEventEnd - perfData.navigationStart;
            console.log(`%cPage Load Time: ${pageLoadTime}ms`, 'color: #10b981; font-weight: bold;');
        }, 0);
    });
}

// Easter egg - Konami code
let konamiCode = [];
const konamiPattern = ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight', 'b', 'a'];

document.addEventListener('keydown', (e) => {
    konamiCode.push(e.key);
    konamiCode = konamiCode.slice(-10);
    
    if (konamiCode.join(',') === konamiPattern.join(',')) {
        document.body.style.animation = 'rainbow 2s linear infinite';
        
        const style = document.createElement('style');
        style.textContent = `
            @keyframes rainbow {
                0% { filter: hue-rotate(0deg); }
                100% { filter: hue-rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
        
        setTimeout(() => {
            document.body.style.animation = '';
        }, 5000);
    }
});

// Add tooltips to complexity badges
document.querySelectorAll('.complexity-badge').forEach(badge => {
    badge.addEventListener('mouseenter', function() {
        this.style.transform = 'scale(1.1)';
        this.style.transition = 'transform 0.2s ease';
    });
    
    badge.addEventListener('mouseleave', function() {
        this.style.transform = 'scale(1)';
    });
});

// Chart animation on scroll
const chartObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, {
    threshold: 0.2
});

document.querySelectorAll('.chart-card').forEach(chart => {
    chart.style.opacity = '0';
    chart.style.transform = 'translateY(30px)';
    chart.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
    chartObserver.observe(chart);
});

// Add pulse animation to stats when they update
const statsObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const statValue = entry.target.querySelector('.stat-value');
            if (statValue) {
                statValue.style.animation = 'pulse 0.5s ease';
            }
        }
    });
}, {
    threshold: 0.5
});

// Add pulse animation keyframes
const pulseStyle = document.createElement('style');
pulseStyle.textContent = `
    @keyframes pulse {
        0%, 100% { transform: scale(1); }
        50% { transform: scale(1.1); }
    }
`;
document.head.appendChild(pulseStyle);

document.querySelectorAll('.stat-card').forEach(stat => {
    statsObserver.observe(stat);
});

// Prevent accidental navigation away
let userInteracted = false;

document.addEventListener('input', () => {
    userInteracted = true;
});

window.addEventListener('beforeunload', (e) => {
    if (userInteracted && (
        document.getElementById('nodeValue').value ||
        document.getElementById('searchValue').value ||
        document.getElementById('deleteValue').value
    )) {
        e.preventDefault();
        e.returnValue = '';
    }
});

// Initialize all components
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        console.log('✅ All components initialized successfully');
    });
} else {
    console.log('✅ All components initialized successfully');
}

// Export functions for HTML onclick handlers
window.scrollToSection = scrollToSection;
window.insertNode = insertNode;
window.searchNode = searchNode;
window.deleteNode = deleteNode;
window.resetTree = resetTree;
