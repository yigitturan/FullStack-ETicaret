function HeroSlider() {
  return (
    <section className="hero">
      <div className="hero-card hero-main">
        <div>
          <p className="hero-label">y11 ozel</p>
          <h1>Teknoloji, moda ve ev urunlerinde firsatlar</h1>
          <p>Guvenli alisveris, hizli sepet ve kolay odeme deneyimi.</p>
        </div>
      </div>

      <div className="hero-card hero-side">
        <h3>Bugune ozel</h3>
        <p>Secili urunlerde avantajli fiyatlar</p>
      </div>

      <div className="hero-card hero-side second">
        <h3>Guvenli Odeme</h3>
        <p>Iyzico sandbox entegrasyonu ile test odemesi</p>
      </div>
    </section>
  );
}

export default HeroSlider;