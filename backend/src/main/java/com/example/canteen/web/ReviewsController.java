package com.example.canteen.web;

import com.example.canteen.dto.Review;
import com.example.canteen.model.ReviewEntity;
import com.example.canteen.repo.ReviewRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

    private final ReviewRepository reviewRepository;

    public ReviewsController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody Review review) {
        String user = review.getUser();
        if (user == null || user.isBlank()) user = "Anonymous";

        ReviewEntity e = new ReviewEntity(review.getShopId(), user, review.getRating(), review.getText());
        reviewRepository.save(e);

        return ResponseEntity.ok(Map.of("status", "saved"));
    }

    @GetMapping("/shop/{shopId}")
    public List<Map<String, Object>> getByShop(@PathVariable int shopId) {
        List<ReviewEntity> list = reviewRepository.findByShopId(shopId);
        List<Map<String, Object>> out = new ArrayList<>();
        for (ReviewEntity r : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("shopId", r.getShopId());
            m.put("user", r.getUserName());
            m.put("rating", r.getRating());
            m.put("text", r.getText() == null ? "" : r.getText());
            m.put("createdAt", r.getCreatedAt() == null ? "" : r.getCreatedAt().toString());
            out.add(m);
        }
        return out;
    }

    @GetMapping("/shop/{shopId}/average")
    public Map<String, Object> average(@PathVariable int shopId) {
        List<ReviewEntity> list = reviewRepository.findByShopId(shopId);
        int count = list.size();
        double avg = count == 0 ? 0 : list.stream().mapToInt(ReviewEntity::getRating).average().orElse(0);
        return Map.<String, Object>of("shopId", shopId, "average", avg, "count", count);
    }

    @GetMapping("/top")
    public List<Map<String, Object>> top() {
        List<ReviewEntity> all = reviewRepository.findAll();
        Map<Integer, Long> counts = new HashMap<>();
        for (ReviewEntity r : all) counts.put(r.getShopId(), counts.getOrDefault(r.getShopId(), 0L) + 1);
        return counts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(e -> Map.<String, Object>of("shopId", e.getKey(), "count", e.getValue()))
                .toList();
    }

    @GetMapping("/recent")
    public List<Map<String, Object>> recent() {
        List<ReviewEntity> all = reviewRepository.findAll();
        return all.stream()
                .sorted(Comparator.comparing(ReviewEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(20)
                .map(r -> Map.<String,Object>of(
                        "shopId", r.getShopId(),
                        "user", r.getUserName(),
                        "rating", r.getRating(),
                        "text", r.getText() == null ? "" : r.getText(),
                        "createdAt", r.getCreatedAt() == null ? "" : r.getCreatedAt().toString()
                ))
                .toList();
    }
}