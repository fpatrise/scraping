package com.fabio.scraping.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelData
{
    private List<List<Object>> data;

    public ExcelData()
    {
        data = new ArrayList<>();
    }

    public void setHeader(List<Object> header) {
       data.add(0, header);
    }

    public List<List<Object>> getData()
    {
        return data;
    }

    public void addRow(List<Object> row)
    {
        data.add(row);
    }

    public void clearData()
    {
        data = new ArrayList<>();
    }
}
