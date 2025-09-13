import React, { useRef, useEffect } from 'react';

interface ParticleBackgroundProps {
  logoSrc?: string;
}

const ParticleBackground: React.FC<ParticleBackgroundProps> = ({ logoSrc }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const resize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };
    resize();
    window.addEventListener('resize', resize);

    type Particle = { x: number; y: number; vx: number; vy: number; size: number; color: string };
    const particles: Particle[] = [];
    const particleCount = 100;
    const colors = ['#31EC56', '#EE72F8'];

    for (let i = 0; i < particleCount; i++) {
      particles.push({
        x: Math.random() * canvas.width,
        y: Math.random() * canvas.height,
        vx: (Math.random() - 0.5) * 2,
        vy: (Math.random() - 0.5) * 2,
        size: Math.random() * 20 + 10,
        color: colors[Math.floor(Math.random() * colors.length)],
      });
    }

    const logo = new Image();
    let logoLoaded = false;
    if (logoSrc) {
      logo.src = logoSrc;
      logo.onload = () => {
        logoLoaded = true;
      };
    }

    const animate = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      particles.forEach(p => {
        p.x += p.vx;
        p.y += p.vy;

        if (p.x < 0 || p.x > canvas.width) p.vx *= -1;
        if (p.y < 0 || p.y > canvas.height) p.vy *= -1;

        if (logoSrc && logoLoaded) {
          ctx.drawImage(logo, p.x - p.size / 2, p.y - p.size / 2, p.size, p.size);
        } else {
          ctx.beginPath();
          ctx.arc(p.x, p.y, p.size / 2, 0, Math.PI * 2);
          ctx.fillStyle = p.color;
          ctx.fill();
        }
      });

      requestAnimationFrame(animate);
    };

    animate();
    return () => window.removeEventListener('resize', resize);
  }, [logoSrc]);

  return <canvas ref={canvasRef} style={{ position: 'fixed', inset: 0, zIndex: -1 }} />;
};

export default ParticleBackground;
