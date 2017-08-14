package com.fabio.scraping.service;

import com.fabio.scraping.excel.ExcelData;
import com.fabio.scraping.excel.ExcelWriter;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BellCADExtractor
{
    private static Logger logger = LoggerFactory.getLogger(BellCADExtractor.class);

    @Autowired
    private ExcelWriter excelWriter;

    @PostConstruct
    public void run()
    {
        List<Integer> propertiesIds = Arrays.asList(227,129272,262,233893,116615);

        ExcelData data = new ExcelData();
        List<Object> header = Arrays.asList("Property_ID", "Owner_Name", "Agricultural Market Valuation", "Land Non-Homesite Value", "Land Homesite Value");
        data.setHeader(header);
        extract(propertiesIds).forEach(data::addRow);

        excelWriter.writeExcel("bellCadData", data);
    }

    public List<Object> extract(Integer propertyId)
    {
        Document doc = get(propertyId);

        if (doc == null) {
            logger.error("Property id not found: "+propertyId);
            return Arrays.asList(propertyId, "not found", "not found", "not found", "not found");
        }
        Elements valuesDetails = doc.getElementById("valuesDetails").children().get(0).children().get(0).children();

        String landHomesiteValue = valuesDetails.get(2).children().get(2).text();
        String landNonHomesiteValue = valuesDetails.get(3).children().get(2).text();
        String agricolturalValue = valuesDetails.get(4).children().get(2).text();

        return Arrays.asList(propertyId, "owner name", landHomesiteValue, landNonHomesiteValue, agricolturalValue);
    }

    public List<List<Object>> extract(List<Integer> ids)
    {
        List<List<Object>> data = new ArrayList<>();
        ids.forEach(id -> {
            data.add(extract(id));
            logger.info("Extracting data from property id: "+id);
        });
        return data;
    }

    public Document get(Integer propertyId)
    {
        try ( WebClient webClient = new WebClient()) {

            HtmlPage page1 = webClient.getPage("http://propaccess.bellcad.org/clientdb/?cid=1");
            HtmlForm form = page1.getFormByName("Form1");

            HtmlSubmitInput button = form.getInputByName("propertySearchOptions:search");
            HtmlTextInput textField = form.getInputByName("propertySearchOptions:propertyid");

            textField.setValueAttribute("227");

            button.click();

            try {
                HtmlPage page2 = webClient.getPage("http://propaccess.bellcad.org/clientdb/Property.aspx?prop_id="+propertyId);
                WebResponse response = page2.getWebResponse();
                String content = response.getContentAsString();
                return Jsoup.parse(content);
            } catch (FailingHttpStatusCodeException e) {
                // do nothing
            }

        } catch (IOException e) {
            logger.error("Error while creating web client", e);
        }

        return null;
    }
}
