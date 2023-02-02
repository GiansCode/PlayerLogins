package io.obadiah.pl.spigot.commands;

import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.storage.data.Platform;
import io.obadiah.pl.common.storage.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerLoginsCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("playerlogins.command")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
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
        return true;

    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage("PlayerLogins Help: ");
        sender.sendMessage(" ");

        sender.sendMessage("/playerlogins info <hostname> - Shows information about where players using <hostname> is located.");
        sender.sendMessage("/playerlogins platform <hostname> - Shows information about the platforms used to join the given <hostname>");
        sender.sendMessage("/playerlogins top - Shows information - Shows information about how many joins each hostname has.");

    }

    private void sendUsage(CommandSender sender, String cmd) {
        if (cmd.equals("info")) {
            sender.sendMessage("Incorrect Usage!");
            sender.sendMessage("/playerlogins info <hostname>");
        }
        else if (cmd.equals("platform")) {
            sender.sendMessage("Incorrect Usage!");
            sender.sendMessage("/playerlogins platform <hostname>");
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
                    if (Bukkit.getPlayer(playerDatum.getUuid()) != null)
                        bedrockOnline++;
                } else {
                    java++;
                    if (Bukkit.getPlayer(playerDatum.getUuid()) != null)
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
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
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
