package main

import (
	"fmt"
	"github.com/PuerkitoBio/goquery"
	"io/ioutil"
	"log"
	"net/http"
	"os/exec"
	"strings"
)

const (
	checkUrlOfTorrentAdServiceWebSiteUrl string = "https://rankers.info/"

	torrentAdConfigFilePath     string = "/usr/local/torrentad/torrentad.xml"
	torrentAdRestartProgramPath string = "/usr/local/torrentad/torrentad-restart.sh"

	openTagOfTorrentAdServiceWebSiteUrl  string = "<website-base-url>"
	closeTagOfTorrentAdServiceWebSiteUrl string = "</website-base-url>"
)

func main() {
	newTorrentAdWebSiteUrl := TorrentAdWebSiteUrl()
	if len(newTorrentAdWebSiteUrl) == 0 {
		log.Fatalf("%s에서 토렌트 사이트의 주소를 얻지 못하였습니다.", checkUrlOfTorrentAdServiceWebSiteUrl)
		return
	}

	fd, err := ioutil.ReadFile(torrentAdConfigFilePath)
	checkErr(err)

	fdLines := strings.Split(string(fd), "\n")

	for i, line := range fdLines {
		if strings.Contains(line, openTagOfTorrentAdServiceWebSiteUrl) {
			pos1 := strings.Index(line, openTagOfTorrentAdServiceWebSiteUrl)
			pos2 := strings.LastIndex(line, closeTagOfTorrentAdServiceWebSiteUrl)
			torrentAdWebSiteUrl := line[pos1+len(openTagOfTorrentAdServiceWebSiteUrl) : pos2]

			if torrentAdWebSiteUrl != newTorrentAdWebSiteUrl {
				fdLines[i] = fmt.Sprintf("\t\t\t%s%s%s", openTagOfTorrentAdServiceWebSiteUrl, newTorrentAdWebSiteUrl, closeTagOfTorrentAdServiceWebSiteUrl)
				log.Printf("토렌트 사이트의 주소가 변경되었습니다(변경전:%s, 변경후:%s)", torrentAdWebSiteUrl, newTorrentAdWebSiteUrl)
				break
			} else {
				log.Println("토렌트 사이트의 주소가 변경되지 않았습니다.")
				return
			}
		}
	}

	fdNew := strings.Join(fdLines, "\n")
	err = ioutil.WriteFile(torrentAdConfigFilePath, []byte(fdNew), 0644)
	checkErr(err)

	err = exec.Command(torrentAdRestartProgramPath).Start()
	checkErr(err)
}

func TorrentAdWebSiteUrl() (ret string) {
	res, err := http.Get(checkUrlOfTorrentAdServiceWebSiteUrl)
	checkErr(err)
	checkCode(res)

	defer res.Body.Close()

	doc, err := goquery.NewDocumentFromReader(res.Body)
	checkErr(err)

	doc.Find("div.centerbox > div.listbox").Each(func(i int, s *goquery.Selection) {
		value, exists := s.Attr("onclick")
		if exists == true {
			if strings.Contains(value, "torrentqq") {
				pos1 := strings.Index(value, "('")
				pos2 := strings.LastIndex(value, "')")
				if pos1 != -1 && pos2 != -1 {
					if value[pos2-1] == '/' {
						pos2 -= 1
					}
					ret = value[pos1+2 : pos2]
					return
				}
			}
		}
	})

	return
}

func checkErr(err error) {
	if err != nil {
		log.Fatalln(err)
	}
}

func checkCode(res *http.Response) {
	if res.StatusCode != 200 {
		log.Fatalln("Request failed with Status:", res.StatusCode)
	}
}
