function ProductCard({ product, onAddToCart, onProductClick }) {
  const fallbackImage =
    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=600&auto=format&fit=crop";

  return (
    <div className="product-card" onClick={() => onProductClick(product)}>
      <div className="image-box">
        <img
          src={product.imageUrl || fallbackImage}
          alt={product.name}
        />
      </div>

      <p className="product-category">{product.category}</p>
      <h3>{product.name}</h3>
      <p className="product-description">{product.description}</p>

      <div className="product-footer">
        <div>
          <p className="price">{product.price} TL</p>
          <p className="stock">Stok: {product.stockQuantity}</p>
        </div>

        <button
          onClick={(e) => {
            e.stopPropagation();
            onAddToCart(product.id);
          }}
        >
          Sepete Ekle
        </button>
      </div>
    </div>
  );
}

export default ProductCard;