package com.think_different.think_different.bucketlist.service;

import com.think_different.think_different.bucketlist.dto.BucketListRecommendationResponseDto;
import com.think_different.think_different.bucketlist.dto.BucketListResponseDto;
import com.think_different.think_different.bucketlist.dto.WeatherDto;
import com.think_different.think_different.bucketlist.entity.BucketList;
import com.think_different.think_different.bucketlist.entity.BucketListPlaceType;
import com.think_different.think_different.bucketlist.entity.BucketListPriority;
import com.think_different.think_different.bucketlist.entity.BucketListSeason;
import com.think_different.think_different.bucketlist.repository.BucketListRepository;
import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 버킷리스트 추천 로직 전용 서비스.
 * 날씨(WeatherService) + 계절 + 평일/주말 조건으로 커플의 미완료 버킷리스트 항목을 필터링/정렬한다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BucketListRecommendationService {

    private static final int RECOMMENDATION_LIMIT = 5;
    private static final int RANDOM_CARD_MIN = 5;
    private static final int RANDOM_CARD_MAX = 6;
    private static final double COLD_THRESHOLD = 0.0;
    private static final double HOT_THRESHOLD = 33.0;
    private static final Set<String> BAD_OUTDOOR_WEATHER = Set.of("RAIN", "DRIZZLE", "THUNDERSTORM", "SNOW");

    private final BucketListRepository bucketListRepository;
    private final CoupleMemberRepository coupleMemberRepository;
    private final WeatherService weatherService;

    public BucketListRecommendationResponseDto recommend(Member member) {

        RecommendationContext context = resolveContext(member);

        List<BucketListResponseDto> recommendations = filterCandidates(context)
                .stream()
                .sorted(recommendationOrder(context.weekend()))
                .limit(RECOMMENDATION_LIMIT)
                .map(BucketListResponseDto::fromBucketList)
                .toList();

        return toResponseDto(context, recommendations);
    }

    /**
     * 대시보드 카드 뒤집기 위젯용: 조건 필터링된 항목 중 5~6개를 무작위로 뽑는다.
     */
    public BucketListRecommendationResponseDto recommendRandomCards(Member member) {

        RecommendationContext context = resolveContext(member);

        List<BucketList> candidates = new ArrayList<>(filterCandidates(context));
        Collections.shuffle(candidates);

        int cardCount = candidates.isEmpty()
                ? 0
                : Math.min(candidates.size(), ThreadLocalRandom.current().nextInt(RANDOM_CARD_MIN, RANDOM_CARD_MAX + 1));

        List<BucketListResponseDto> cards = candidates.stream()
                .limit(cardCount)
                .map(BucketListResponseDto::fromBucketList)
                .toList();

        return toResponseDto(context, cards);
    }

    private RecommendationContext resolveContext(Member member) {

        Couple couple = coupleMemberRepository.findConnectedByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."))
                .getCouple();

        LocalDate today = LocalDate.now();
        BucketListSeason currentSeason = BucketListSeason.fromMonth(today.getMonthValue());
        boolean weekend = today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY;

        WeatherDto weather = weatherService.getCurrentWeather();
        boolean outdoorFriendly = isOutdoorFriendly(weather);

        return new RecommendationContext(couple, currentSeason, weekend, weather, outdoorFriendly);
    }

    private List<BucketList> filterCandidates(RecommendationContext context) {
        return bucketListRepository.findByCoupleAndCompletedFalse(context.couple())
                .stream()
                .filter(bucketList -> matchesSeason(bucketList, context.season()))
                .filter(bucketList -> matchesWeather(bucketList, context.outdoorFriendly()))
                .toList();
    }

    private BucketListRecommendationResponseDto toResponseDto(RecommendationContext context,
                                                               List<BucketListResponseDto> items) {
        return BucketListRecommendationResponseDto.builder()
                .recommendations(items)
                .season(context.season())
                .weekend(context.weekend())
                .weather(context.weather())
                .build();
    }

    private record RecommendationContext(Couple couple,
                                          BucketListSeason season,
                                          boolean weekend,
                                          WeatherDto weather,
                                          boolean outdoorFriendly) {
    }

    private boolean isOutdoorFriendly(WeatherDto weather) {
        if (weather.isUnknown()) {
            return true;
        }

        if (BAD_OUTDOOR_WEATHER.contains(weather.getMain().toUpperCase())) {
            return false;
        }

        Double temperature = weather.getTemperature();

        return temperature == null || (temperature >= COLD_THRESHOLD && temperature <= HOT_THRESHOLD);
    }

    private boolean matchesSeason(BucketList bucketList, BucketListSeason currentSeason) {
        return bucketList.getSeason() == currentSeason || bucketList.getSeason() == BucketListSeason.ALL_SEASON;
    }

    private boolean matchesWeather(BucketList bucketList, boolean outdoorFriendly) {
        return bucketList.getPlaceType() == BucketListPlaceType.INDOOR || outdoorFriendly;
    }

    private Comparator<BucketList> recommendationOrder(boolean weekend) {
        return Comparator
                .comparingInt((BucketList bucketList) -> priorityWeight(bucketList.getPriority()))
                .thenComparingInt(bucketList -> placeTypeWeight(bucketList.getPlaceType(), weekend));
    }

    private int priorityWeight(BucketListPriority priority) {
        return switch (priority) {
            case MUST_DO -> 0;
            case SOMEDAY -> 1;
            case HOLD -> 2;
        };
    }

    private int placeTypeWeight(BucketListPlaceType placeType, boolean weekend) {
        boolean outdoorFirst = weekend;
        boolean isOutdoor = placeType == BucketListPlaceType.OUTDOOR;

        return (isOutdoor == outdoorFirst) ? 0 : 1;
    }
}
