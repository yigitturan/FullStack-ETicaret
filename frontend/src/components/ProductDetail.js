function ProductDetail({ product, onBack, onAddToCart }) {
  const fallbackImage =
    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=900&auto=format&fit=crop";

  if (!product) {
    return (
      <main className="product-detail-page">
        <div className="product-detail-card">
          <h2>Urun bulunamadi</h2>
          <button onClick={onBack}>Ana Sayfaya Don</button>
        </div>
      </main>
    );
  }

  return (
    <main className="product-detail-page">
      <button className="back-btn" onClick={onBack}>
        ← Alisverise Don
      </button>

      <section className="product-detail-card">
        <div className="product-detail-image">
          <img src={product.imageUrl || fallbackImage} alt={product.name} />
        </div>

        <div className="product-detail-info">
          <p className="product-category">{product.category}</p>

          <h1>{product.name}</h1>

          <p className="detail-description">
            {product.description}
          </p>

          <div className="detail-info-grid">
            <div>
              <span>Fiyat</span>
              <b>{product.price} TL</b>
            </div>

            <div>
              <span>Stok</span>
              <b>{product.stockQuantity}</b>
            </div>

            <div>
              <span>Kategori</span>
              <b>{product.category}</b>
            </div>
          </div>

          <button
            className="detail-cart-btn"
            onClick={() => onAddToCart(product.id)}
          >
            Sepete Ekle
          </button>
        </div>
      </section>
    </main>
  );
}

export default ProductDetail;