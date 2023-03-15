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

        arcadeService.openArcade(arcadeId);

        return ApiResponse.succ("Open Arcade OK");
    }

    @PostMapping("/queue")
    public ApiResponse<String> playArcadeWithQueue(@RequestParam Long userId,
                                                   @RequestParam Long arcadeId){

        arcadeService.playArcade(userId,arcadeId);
        return ApiResponse.succ("play Arcade OK");
    }

    @PostMapping("/close/{arcadeId}")
    public ApiResponse<String> closeArcade(@PathVariable Long arcadeId){

        arcadeService.closeArcade(arcadeId);
        return ApiResponse.succ("play Arcade OK");
    }

    @PostMapping("")
    public ApiResponse<String> playArcade(@RequestParam Long userId,
                                          @RequestParam Long arcadeId){

        return ApiResponse.succ("play Arcade OK");
    }

    @GetMapping("/{userId}")
    public ApiResponse<String> getRankOrStatus(@PathVariable Long userId){

        return ApiResponse.succ("TODO put service method");
    }


}
