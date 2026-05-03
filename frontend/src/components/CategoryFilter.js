function CategoryFilter({ categories, selectedCategory, setSelectedCategory }) {
  const getIcon = (category) => {
    const lower = category.toLowerCase();

    if (lower.includes("telefon") || lower.includes("phone")) return "📱";
    if (lower.includes("bilgisayar") || lower.includes("computer")) return "💻";
    if (lower.includes("moda") || lower.includes("giyim")) return "👕";
    if (lower.includes("ev")) return "🏠";
    if (lower.includes("spor")) return "⚽";
    if (lower.includes("kozmetik")) return "💄";
    if (lower.includes("kitap")) return "📚";
    if (lower.includes("elektronik")) return "🎧";
    if (lower.includes("tumu")) return "✨";

    return "🛍️";
  };

  return (
    <div className="category-bar">
      {categories.map((category) => (
        <button
          key={category}
          className={selectedCategory === category ? "category active" : "category"}
          onClick={() => setSelectedCategory(category)}
        >
          <span>{getIcon(category)}</span>
          {category}
        </button>
      ))}
    </div>
  );
}

export default CategoryFilter;