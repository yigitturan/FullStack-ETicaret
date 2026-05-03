import api from "./api";

// sepetten siparis olusturuyoruz
export const checkoutMyCart = async () => {
    const response = await api.post("/api/orders/checkout/my-cart");
    return response.data;
};