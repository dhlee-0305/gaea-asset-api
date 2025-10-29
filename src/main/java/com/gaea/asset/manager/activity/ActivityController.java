package com.gaea.asset.manager.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.activity.service.ActivityService;
import com.gaea.asset.manager.activity.vo.ActivityVO;
import com.gaea.asset.manager.util.Header;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "활동 정보 API", description = "Live Activity 및 Now Bar를 위한 활동 정보 API 입니다.")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/activity")
    @Operation(summary = "활동 정보 조회", description = "Live Activity를 위한 현재 활동 정보 조회 API")
    public Header<ActivityVO> getActivityInfo() {
        return activityService.getActivityInfo();
    }
}
