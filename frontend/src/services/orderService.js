import api from "./api";

// sepetten siparis olusturuyoruz
export const checkout = async (cartId) => {
    const response = await api.post(`/api/orders/checkout/${cartId}`);
    return response.data;
};