package com.taotao.search.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taotao.search.pojo.SearchResult;
import com.taotao.search.service.SearchService;

@RestController
public class SearchController {

	@Autowired
	private SearchService searchService;

	@RequestMapping(value = "/q", method = RequestMethod.GET)
	public TaotaoResult search(@RequestParam(defaultValue = "") String queryString, @RequestParam(defaultValue = "1") Integer page,
							   @RequestParam(defaultValue = "60") Integer rows) {
		//查询条件不能为空
		if (StringUtils.isBlank(queryString)) {
			return TaotaoResult.build(400, "查询条件不能为空");
		}
		SearchResult searchResult;
		try {
			queryString = new String(queryString.getBytes("iso8859-1"), "utf-8");
			searchResult = searchService.search(queryString, page, rows);
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		return TaotaoResult.ok(searchResult);
	}

}