package com.aegis.modules.notice.controller;

import com.aegis.modules.notice.service.NoticeService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/10 14:09
 * @Description: 通知接口
 */
@RestController
@Api(tags = "通知接口")
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
}
