package com.yigit.ecommerce.assistant.service.impl;

import com.yigit.ecommerce.assistant.dto.AssistantRequest;
import com.yigit.ecommerce.assistant.dto.AssistantResponse;
import com.yigit.ecommerce.assistant.service.IAssistantService;
import com.yigit.ecommerce.assistant.service.OpenAIService;
import com.yigit.ecommerce.product.entity.Product;
import com.yigit.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class AssistantServiceImpl implements IAssistantService {

    private final ProductRepository productRepository;
    private final OpenAIService openAIService;

    public AssistantServiceImpl(ProductRepository productRepository,
                                OpenAIService openAIService) {
        this.productRepository = productRepository;
        this.openAIService = openAIService;
    }

    @Override
    public AssistantResponse ask(AssistantRequest request) {

        // Kullanicidan gelen mesaji kucuk harfe ceviriyorum.
        // Boylece "Telefon", "telefon", "TELEFON" fark etmeden kontrol edebilirim.
        String message = request.getMessage().toLowerCase(Locale.ROOT);

        // Asistan urunleri gercek veritabanindan okusun diye ProductRepository kullaniyorum.
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return new AssistantResponse("Su an sistemde urun bulunmuyor.");
        }

        try {
            // Once gercek OpenAI cevabi almaya calisiyorum.
            // API key yoksa, limit varsa veya OpenAI hata verirse catch bloguna duser.
            String prompt = buildOpenAIPrompt(message, products);
            String aiAnswer = openAIService.askGPT(prompt);

            return new AssistantResponse(aiAnswer);

        } catch (Exception e) {
            // OpenAI calismazsa proje patlamasin diye eski rule-based assistant devreye giriyor.
            // Bu sayede uygulama her durumda cevap verebilir.
        }

        // Buradan sonrasi fallback logic.
        // Yani OpenAI calismazsa basit kurallarla urun onerisi yapiyorum.

        if (message.contains("ucuz") || message.contains("en uygun")) {
            return new AssistantResponse(buildProductAnswer(
                    "En uygun fiyatli urunler:",
                    products.stream()
                            .sorted(Comparator.comparing(Product::getPrice))
                            .limit(3)
                            .toList()
            ));
        }

        if (message.contains("pahali") || message.contains("en pahali")) {
            return new AssistantResponse(buildProductAnswer(
                    "En yuksek fiyatli urunler:",
                    products.stream()
                            .sorted(Comparator.comparing(Product::getPrice).reversed())
                            .limit(3)
                            .toList()
            ));
        }

        if (message.contains("telefon")) {
            return categoryAnswer(products, "Telefon");
        }

        if (message.contains("bilgisayar") || message.contains("laptop")) {
            return categoryAnswer(products, "Bilgisayar");
        }

        if (message.contains("spor")) {
            return categoryAnswer(products, "Spor");
        }

        return new AssistantResponse(buildProductAnswer(
                "Sana genel olarak onerebilecegim urunler:",
                products.stream()
                        .limit(5)
                        .toList()
        ));
    }

    private AssistantResponse categoryAnswer(List<Product> products, String categoryName) {

        // Kategori ismine gore urunleri filtreliyorum.
        List<Product> filteredProducts = products.stream()
                .filter(product -> product.getCategory() != null)
                .filter(product -> product.getCategory().equalsIgnoreCase(categoryName))
                .limit(5)
                .toList();

        if (filteredProducts.isEmpty()) {
            return new AssistantResponse(categoryName + " kategorisinde su an urun bulamadim.");
        }

        return new AssistantResponse(buildProductAnswer(
                categoryName + " kategorisinde onerebilecegim urunler:",
                filteredProducts
        ));
    }

    private String buildProductAnswer(String title, List<Product> products) {

        // Urun listesini kullaniciya okunabilir cevap formatina ceviriyorum.
        StringBuilder answer = new StringBuilder(title);

        for (Product product : products) {
            answer.append("\n- ")
                    .append(product.getName())
                    .append(" | ")
                    .append(product.getPrice())
                    .append(" TL");
        }

        return answer.toString();
    }

    private String buildOpenAIPrompt(String message, List<Product> products) {

        // OpenAI'ye gonderilecek promptu burada hazirliyorum.
        // Amac: Asistan sadece bizim veritabanimizdaki urunlere gore cevap versin.
        StringBuilder prompt = new StringBuilder();

        prompt.append("Sen y11 isimli e-ticaret sitesinde calisan yardimci bir urun asistanisin.\n");
        prompt.append("Kullaniciya sadece asagidaki urun listesine gore cevap ver.\n");
        prompt.append("Listede olmayan urunleri varmis gibi soyleme.\n");
        prompt.append("Cevaplari Turkce, kisa, samimi ve kullanici dostu ver.\n");
        prompt.append("Gereksiz uzun aciklama yapma.\n\n");

        prompt.append("Urun listesi:\n");

        for (Product product : products) {
            prompt.append("- ")
                    .append(product.getName())
                    .append(" | kategori: ")
                    .append(product.getCategory())
                    .append(" | fiyat: ")
                    .append(product.getPrice())
                    .append(" TL\n");
        }

        prompt.append("\nKullanici sorusu: ");
        prompt.append(message);

        return prompt.toString();
    }
}