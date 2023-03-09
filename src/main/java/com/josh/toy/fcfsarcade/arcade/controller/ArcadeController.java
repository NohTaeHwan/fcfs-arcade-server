package com.josh.toy.fcfsarcade.arcade.controller;

import com.josh.toy.fcfsarcade.arcade.service.ArcadeService;
import com.josh.toy.fcfsarcade.common.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/arcade")
public class ArcadeController {

    private final ArcadeService arcadeService;

    @PostMapping("/open/{arcadeId}")
    public ApiResponse<String> openArcade(@PathVariable Long arcadeId){

        //TODO 만든 해시값을 WaitQueue 이름에 사용
        arcadeService.openArcade(arcadeId);

        return ApiResponse.succ("Open Arcade OK");
    }

    @PostMapping("/queue/{userId}")
    public ApiResponse<String> playArcadeWithQueue(@PathVariable Long userId){


        return ApiResponse.succ("play Arcade OK");
    }

    @PostMapping("/{userId}")
    public ApiResponse<String> playArcade(@PathVariable Long userId){


        return ApiResponse.succ("play Arcade OK");
    }

    @GetMapping("/{userId}")
    public ApiResponse<String> getRankOrStatus(@PathVariable Long userId){

        return ApiResponse.succ("TODO put service method");
    }


}