import ProductCard from "./ProductCard";

function ProductList({ products, loading, onAddToCart, onProductClick }) {
  if (loading) {
    return <p className="info-text">Urunler yukleniyor...</p>;
  }

  if (products.length === 0) {
    return <p className="info-text">Urun bulunamadi.</p>;
  }

  return (
    <div className="product-grid">
      {products.map((product) => (
        <ProductCard
          key={product.id}
          product={product}
          onAddToCart={onAddToCart}
          onProductClick={onProductClick}
        />
      ))}
    </div>
  );
}

export default ProductList;