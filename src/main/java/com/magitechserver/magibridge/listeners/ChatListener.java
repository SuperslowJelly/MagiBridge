package com.magitechserver.magibridge.listeners;

import br.net.fabiozumbi12.UltimateChat.API.SendChannelMessageEvent;
import com.magitechserver.magibridge.DiscordHandler;
import com.magitechserver.magibridge.MagiBridge;
import com.magitechserver.magibridge.util.Config;
import com.magitechserver.magibridge.util.GroupUtil;
import com.magitechserver.magibridge.util.Webhooking;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;

import java.util.Map;

/**
 * Created by Frani on 05/07/2017.
 */
public class ChatListener {

    @Listener
    public void onMessage(SendChannelMessageEvent e) {
        // Tell
        if(e.getChannel() == null) return;

        String discordChannel = getKey(e.getChannel().getName().toLowerCase());
        if(discordChannel != null && getUCMessage(e) != null) {
            String prefix = null;
            prefix = ((Player) e.getSender()).getOption("prefix").orElse("");
            if(Config.useWebhooks()) {
                Webhooking.sendWebhookMessage(MagiBridge.getConfig().getString("messages", "webhook-name")
                        .replace("%prefix%", prefix)
                        .replace("%player%", e.getSender().getName()
                        .replace("%topgroup%", GroupUtil.getHighestGroup((Player) e.getSender()))),
                        e.getSender().getName(),
                        getUCMessage(e),
                        discordChannel);
                return;
            }
            DiscordHandler.sendMessageToChannel(discordChannel, getUCMessage(e));
        }
    }


    private String getUCMessage(SendChannelMessageEvent e) {
        if(e.getSender() instanceof Player) {
            String content = e.getMessage();
            String player = e.getSender().getName();
            String prefix = ((Player) e.getSender()).getOption("prefix").orElse("");
            String message = "";
            if(Config.useWebhooks()) {
                message = content;
            } else {
                message = MagiBridge.getConfig().getString("messages", "server-to-discord-format")
                        .replace("%player%", player)
                        .replace("%prefix%", prefix)
                        .replace("%message%", content)
                        .replace("%topgroup%", GroupUtil.getHighestGroup((Player) e.getSender()));
            }
            return message;
        }
        return null;
    }

    private String getKey(String value) {
        for (Map.Entry<String, String> values : MagiBridge.getConfig().getMap("channel", "ultimatechat").entrySet()) {
            if(value.equals(values.getValue().toLowerCase())) {
                return values.getKey();
            }
        }
        return null;
    }
}
