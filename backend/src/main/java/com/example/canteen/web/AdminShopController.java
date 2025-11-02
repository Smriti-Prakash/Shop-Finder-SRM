package com.example.canteen.web;

import com.example.canteen.model.Shop;
import com.example.canteen.repo.ShopRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/shops")
public class AdminShopController {

    private final ShopRepository shopRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminShopController(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    private Map<String, Object> toMap(Shop s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("name", s.getName());
        m.put("category", s.getCategory());
        m.put("lat", s.getLat());
        m.put("lng", s.getLng());
        m.put("image", s.getImage());
        try {
            m.put("menu", s.getMenu() == null || s.getMenu().isBlank() ? Collections.emptyList() : objectMapper.readValue(s.getMenu(), new TypeReference<List<Object>>(){}));
        } catch (Exception e) {
            m.put("menu", Collections.emptyList());
        }
        try {
            m.put("subshops", s.getSubshops() == null || s.getSubshops().isBlank() ? Collections.emptyList() : objectMapper.readValue(s.getSubshops(), new TypeReference<List<Object>>(){}));
        } catch (Exception e) {
            m.put("subshops", Collections.emptyList());
        }
        return m;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        List<Shop> all = shopRepository.findAll();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Shop s : all) out.add(toMap(s));
        return out;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String name = Objects.toString(body.getOrDefault("name", ""));
        String category = Objects.toString(body.getOrDefault("category", ""));
        Double lat = body.get("lat") == null ? null : Double.valueOf(body.get("lat").toString());
        Double lng = body.get("lng") == null ? null : Double.valueOf(body.get("lng").toString());
        String image = Objects.toString(body.getOrDefault("image", ""));
        try {
            String menuJson = objectMapper.writeValueAsString(body.getOrDefault("menu", Collections.emptyList()));
            String subshopsJson = objectMapper.writeValueAsString(body.getOrDefault("subshops", Collections.emptyList()));
            Shop s = new Shop(name, category, lat, lng, image, menuJson, subshopsJson);
            Shop saved = shopRepository.save(s);
            return ResponseEntity.ok(toMap(saved));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "failed_to_save", "detail", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!shopRepository.existsById(id)) return ResponseEntity.notFound().build();
        shopRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Bulk import shops. Accepts an array of shop-like objects from the frontend.
     * Skips exact-name duplicates (case-sensitive). Returns number of inserted rows.
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkImport(@RequestBody List<Map<String, Object>> payload) {
        if (payload == null || payload.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "empty_payload"));
        try {
            // Load existing names to avoid duplicates
            List<String> existingNames = shopRepository.findAll().stream().map(Shop::getName).filter(Objects::nonNull).collect(Collectors.toList());
            int added = 0;
            for (Map<String, Object> item : payload) {
                String name = Objects.toString(item.getOrDefault("name", ""));
                if (name.isBlank()) continue;
                if (existingNames.contains(name)) continue; // skip exact-name duplicates
                String category = Objects.toString(item.getOrDefault("category", ""));
                Double lat = null;
                Double lng = null;
                try { if (item.get("lat") != null) lat = Double.valueOf(item.get("lat").toString()); } catch (Exception ignored) {}
                try { if (item.get("lng") != null) lng = Double.valueOf(item.get("lng").toString()); } catch (Exception ignored) {}
                String image = Objects.toString(item.getOrDefault("image", ""));
                String menuJson = "[]";
                String subshopsJson = "[]";
                try { menuJson = objectMapper.writeValueAsString(item.getOrDefault("menu", Collections.emptyList())); } catch (Exception e) { menuJson = "[]"; }
                try { subshopsJson = objectMapper.writeValueAsString(item.getOrDefault("subshops", Collections.emptyList())); } catch (Exception e) { subshopsJson = "[]"; }
                Shop s = new Shop(name, category, lat, lng, image, menuJson, subshopsJson);
                shopRepository.save(s);
                existingNames.add(name);
                added++;
            }
            return ResponseEntity.ok(Map.of("added", added));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "import_failed", "detail", e.getMessage()));
        }
    }
}
