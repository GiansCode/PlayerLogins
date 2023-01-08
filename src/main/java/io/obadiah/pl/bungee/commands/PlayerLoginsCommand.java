package io.obadiah.pl.bungee.commands;

import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.storage.data.Platform;
import io.obadiah.pl.common.storage.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerLoginsCommand extends Command implements TabExecutor {

    public PlayerLoginsCommand() {
        super("bplayerlogins");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("playerlogins.command")) {
            sender.sendMessage(new ComponentBuilder().color(ChatColor.RED).append("You do not have permission to use that command!").create());
            return;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "top": {
                top(sender, 1);
                break;
            }

            case "info": {
                if (args.length == 1) {
                    sendUsage(sender, "info");
                }
                info(sender, args[1]);
                break;
            }

            case "platform": {
                if (args.length == 1) {
                    sendUsage(sender, "platform");
                }
                platform(sender, args[1]);
                break;
            }

            default:
                sendHelp(sender);
        }


    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage("PlayerLogins Help: ");
        sender.sendMessage(" ");

        sender.sendMessage("/bplayerlogins info <hostname> - Shows information about where players using <hostname> is located.");
        sender.sendMessage("/bplayerlogins platform <hostname> - Shows information about the platforms used to join the given <hostname>");
        sender.sendMessage("/bplayerlogins top - Shows information - Shows information about how many joins each hostname has.");

    }

    private void sendUsage(CommandSender sender, String cmd) {
        if (cmd.equals("info")) {
            sender.sendMessage("Incorrect Usage!");
            sender.sendMessage("/bplayerlogins info <hostname>");
        }
        else if (cmd.equals("platform")) {
            sender.sendMessage("Incorrect Usage!");
            sender.sendMessage("/bplayerlogins platform <hostname>");
        }
    }

    private void top(CommandSender sender, int page) {
        sender.sendMessage("  ");
        sender.sendMessage("Top Joins");
        sender.sendMessage("  ");

        int totalJoins = 0;

        for (Integer value : PlayerLoginsCore.getHostnameStorage().getHostnames().values()) {
            totalJoins += value;
        }

        sender.sendMessage("Total Joins: " + totalJoins);
        sender.sendMessage("  ");

        int finalTotalJoins = totalJoins;

        PlayerLoginsCore.getHostnameStorage().getHostnames().forEach((s, integer) -> {
            sender.sendMessage(s + ": " + integer + " (" + fractionToPercent(integer, finalTotalJoins) + ")");
        });

    }

    private void info(CommandSender sender, String ip) {
        sender.sendMessage("  ");
        sender.sendMessage("Global statistics for " + ip);
        sender.sendMessage("  ");

        int playerCount = 0;
        Map<String, Integer> regionMap = new HashMap<>();

        for (PlayerData playerDatum : PlayerLoginsCore.getPlayerStorage().getPlayerData()) {
            if (!playerDatum.getLastJoin().equalsIgnoreCase(ip))
                continue;
            regionMap.compute(playerDatum.getLastLocation(), (s, integer) -> (integer == null) ? 1 : ++integer);
            playerCount++;
        }

        sender.sendMessage("Total Players: " + playerCount);

        List<Map.Entry<String, Integer>> sorted = regionMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

        for (Map.Entry<String, Integer> stringIntegerEntry : sorted) {
            sender.sendMessage(stringIntegerEntry.getKey() + ": " + stringIntegerEntry.getValue() + "(" + fractionToPercent(stringIntegerEntry.getValue(), playerCount) + ")");
        }


    }

    private void platform(CommandSender sender, String ip) {
        sender.sendMessage("  ");
        sender.sendMessage("Platform Breakdown for " + ip);
        int bedrock = 0, java = 0, bedrockOnline = 0, javaOnline = 0;
        for (PlayerData playerDatum : PlayerLoginsCore.getPlayerStorage().getPlayerData()) {
            if (playerDatum.getLastJoin().equalsIgnoreCase(ip)) {
                if (playerDatum.getPlatform() == Platform.BEDROCK) {
                    bedrock++;
                    if (ProxyServer.getInstance().getPlayer(playerDatum.getUuid()) != null)
                        bedrockOnline++;
                } else {
                    java++;
                    if (ProxyServer.getInstance().getPlayer(playerDatum.getUuid()) != null)
                        javaOnline++;
                }
            }
        }

        sender.sendMessage("  ");
        sender.sendMessage("Total Players: " + (bedrock + java));
        sender.sendMessage(" ");
        sender.sendMessage("Java Players: " + java + " (" + fractionToPercent(java, java + bedrock) + ") [" + javaOnline + " Online]");
        sender.sendMessage("Bedrock Players: " + bedrock + " (" + fractionToPercent(bedrock, java + bedrock) + ") [" + bedrockOnline + " Online]");
    }

    private final DecimalFormat format = new DecimalFormat("#.00");

    private String fractionToPercent(int numerator, int denominator) {
        if (denominator == 0)
            return "0%";
        return format.format((double) numerator / (double) denominator * 100D) + "%";
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return Stream.of("info", "platform", "top").filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("platform")) {
                return PlayerLoginsCore.getHostnameStorage().getHostnames().keySet().stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }
}
