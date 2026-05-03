import api from "./api";

// urunu sepete ekliyoruz
export const addToCart = async (productId, quantity = 1) => {
    const response = await api.post("/api/cart/add", {
        productId,
        quantity
    });

    return response.data;
};

// login olan kullanicinin kendi sepetini getiriyoruz
export const getMyCart = async () => {
    const response = await api.get("/api/cart/my-cart");
    return response.data;
};

// eski yapi kalsin diye bunu simdilik silmiyorum
export const getCart = async (cartId) => {
    const response = await api.get(`/api/cart/${cartId}`);
    return response.data;
};

// sepetten urun siliyoruz
export const removeFromCart = async (cartItemId) => {
    const response = await api.delete(`/api/cart/item/${cartItemId}`);
    return response.data;
};