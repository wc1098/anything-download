package top.cyblogs.download.downloader;

import com.fasterxml.jackson.databind.JsonNode;
import top.cyblogs.api.AuApi;
import top.cyblogs.data.BiliBiliData;
import top.cyblogs.data.DownloadList;
import top.cyblogs.data.SettingsData;
import top.cyblogs.model.DownloadItem;
import top.cyblogs.model.TempDownloadItem;
import top.cyblogs.model.enums.DownloadType;
import top.cyblogs.model.enums.ServiceType;
import top.cyblogs.util.StringUtils;
import top.cyblogs.utils.BiliBiliUtils;

import java.io.File;

public class AuDownloader {

    public static void download(String url) {
        String playId = BiliBiliUtils.getPlayId(url);
        JsonNode musicDetail = AuApi.getMusicDetail(playId);
        JsonNode detailData = musicDetail.findValue("data");
        String title = detailData.findValue("title").asText();
        String author = detailData.findValue("author").asText();
        if (StringUtils.isNotEmpty(author)) {
            title = author + " - " + title;
        }
        long aid = detailData.findValue("aid").asLong();
        if (aid > 0) {
            AvDownloader.download("https://www.bilibili.com/video/av" + aid);
        }
        JsonNode musicUrl = AuApi.getMusicUrl(playId);
        String downloadUrl = musicUrl.findValue("data").findValue("cdns").get(0).asText();

        DownloadItem mp3Status = new DownloadItem();
        mp3Status.setSource(BiliBiliData.SOURCE);
        mp3Status.setDownloadType(DownloadType.AUDIO);
        File targetFile = new File(SettingsData.path + title + ".mp3");
        String downloadId = StringUtils.md5(targetFile.getAbsolutePath());
        TempDownloadItem tempDownloadItem = new TempDownloadItem(downloadId, targetFile.getName(), ServiceType.NORMAL, downloadUrl, null, targetFile, BiliBiliData.header(), mp3Status);
        DownloadList.tempList.add(tempDownloadItem);
    }
}
