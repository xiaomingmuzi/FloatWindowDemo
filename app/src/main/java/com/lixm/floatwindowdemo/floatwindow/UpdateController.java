package com.lixm.floatwindowdemo.floatwindow;

/**
 * Describe:FloatLayout交互接口
 * <p>
 * Author: Lixm
 * Date: 2019/7/24
 */
public interface UpdateController {
    void onFloatUpdate(int x, int y);
    void onClick();
    void onClose();
}
