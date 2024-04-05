package com.lothus.snow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;

@Getter @Setter
@AllArgsConstructor
public class SnowmanInfo {

    private Player player;
    private Snowman snowman;
    private long delete;
}
