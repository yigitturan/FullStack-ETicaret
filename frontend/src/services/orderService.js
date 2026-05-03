import api from "./api";

// sepetteki urunlerden siparis olusturuyoruz
export const checkout = async (cartId) => {
    const response = await api.post(`/api/orders/checkout/${cartId}`);
    return response.data;
};