package com.darkkaiser.torrentad.website;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public enum WebSite {

	TORRENT("토렌트",
			"com.darkkaiser.torrentad.website.impl.torrent.Torrent",
			"com.darkkaiser.torrentad.website.impl.torrent.TorrentBoard"),

	BOGOBOGO("보고보고",
			 "com.darkkaiser.torrentad.website.impl.bogobogo.BogoBogo",
			 "com.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard"),

	TOTORIA("토토리아",
			"com.darkkaiser.torrentad.website.impl.totoria.Totoria",
			"com.darkkaiser.torrentad.website.impl.totoria.TotoriaBoard"),

	TORRENTMAP("토렌트맵",
			"com.darkkaiser.torrentad.website.impl.torrentmap.TorrentMap",
			"com.darkkaiser.torrentad.website.impl.torrentmap.TorrentMapBoard"),

	TORRENTMI("토렌트미",
			"com.darkkaiser.torrentad.website.impl.torrentmi.TorrentMi",
			"com.darkkaiser.torrentad.website.impl.torrentmi.TorrentMiBoard"),

	TORRENTBLACK("토렌트블랙",
			"com.darkkaiser.torrentad.website.impl.torrentblack.TorrentBlack",
			"com.darkkaiser.torrentad.website.impl.torrentblack.TorrentBlackBoard"),

	TORRENTBE("토렌트비",
			"com.darkkaiser.torrentad.website.impl.torrentbe.TorrentBe",
			"com.darkkaiser.torrentad.website.impl.torrentbe.TorrentBeBoard"),

	TORRENTHALL("토렌트홀",
			"com.darkkaiser.torrentad.website.impl.torrenthall.TorrentHall",
			"com.darkkaiser.torrentad.website.impl.torrenthall.TorrentHallBoard"),

	TORRENTQQ("토렌트큐큐",
			"com.darkkaiser.torrentad.website.impl.torrentqq.TorrentQQ",
			"com.darkkaiser.torrentad.website.impl.torrentqq.TorrentQQBoard"),

	TODAWA("토다와",
			"com.darkkaiser.torrentad.website.impl.todawa.Todawa",
			"com.darkkaiser.torrentad.website.impl.todawa.TodawaBoard");

	@Getter
	private final String name;
	private final String webSiteClassName;
	private final String webSiteBoardClassName;

	@Getter
	@Setter
	private String baseURL;

	public static WebSite fromString(final String name) {
		Objects.requireNonNull(name, "name");

		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final WebSite site : WebSite.values()) {
			if (name.equals(site.getName()) == true)
				return site;
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSite.class.getSimpleName(), name));
	}

	public WebSiteAccount createAccount(final String id, final String password) {
		return new DefaultWebSiteAccount(id, password);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public WebSiteConnection createConnection(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		try {
			final Class clazz = Class.forName(this.webSiteClassName);
			final Constructor constructor = clazz.getConstructor(WebSiteConnector.class, String.class, String.class);
			return new RetryLoginOnNoPermissionWebSite((AbstractWebSite) constructor.newInstance(siteConnector, owner, downloadFileWriteLocation));
		} catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			log.error(null, e);
			return null;
		}
	}
	
	public WebSiteSearchContext createSearchContext() {
        return new DefaultWebSiteSearchContext(this);
    }
	
	public WebSiteSearchKeywords createSearchKeywords(final String modeValue) {
		return new DefaultWebSiteSearchKeywords(WebSiteSearchKeywordsMode.fromString(modeValue));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public WebSiteBoard getBoardByName(final String name) {
		try {
			final Class clazz = Class.forName(this.webSiteBoardClassName);
			final Method method = clazz.getDeclaredMethod("fromString", String.class);
			return (WebSiteBoard) method.invoke(null, name);
		} catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			log.error(null, e);
			return null;
		}
	}

	public WebSiteBoard getBoardByCode(final String code) {
		if (StringUtil.isBlank(code) == true)
			throw new IllegalArgumentException("code는 빈 문자열을 허용하지 않습니다.");

		WebSiteBoard[] boardValues = getBoardValues();
		for (final WebSiteBoard board : Objects.requireNonNull(boardValues)) {
			if (board.getCode().equals(code) == true)
				return board;
		}

		return null;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public WebSiteBoard[] getBoardValues() {
		try {
			final Class clazz = Class.forName(this.webSiteBoardClassName);
			final Method method = clazz.getDeclaredMethod("values");
			return (WebSiteBoard[]) method.invoke(null);
		} catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			log.error(null, e);
			return null;
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}
