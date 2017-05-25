package com.chariot.shadow;

import com.chariot.shadow.date.DatePattern;
import com.chariot.shadow.news.News;
import com.chariot.shadow.news.NewsApplication;
import com.chariot.shadow.supplier.*;
import com.sun.syndication.io.FeedException;
import lombok.Value;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * News Bootstrap to start news crawler system
 * <p>
 * Created by Trung Vu on 2017/05/25.
 */
@Value
public class NewsBootstrap {

    private File workingFile;
    private Supplier supplier;
    private Date from;
    private Date to;

    public static void main(String[] args) throws IOException, FeedException {
        if (args.length < 4)
            throw new IllegalArgumentException("Not enough arguments. Required: working directory, supplier ID, from, to");

        File workingDirecory = new File(args[0]).getAbsoluteFile();
        if (!workingDirecory.exists() || !workingDirecory.isDirectory())
            throw new IllegalArgumentException(
                    "working directory doesn't exist or not a directory: " + workingDirecory);

        SupplierType supplier = SupplierType.get(Integer.valueOf(args[1]));

        Date from, to;
        try {
            from = DatePattern.generateFormatter(DatePattern.YYYY_MM_DD).parse(args[2]);
            to = DatePattern.generateFormatter(DatePattern.YYYY_MM_DD).parse(args[3]);

            if (from.compareTo(to) >= 0)
                throw new IllegalArgumentException("Invalid date range: from "
                        + from + " to " + to);
        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. The format must be yyyyMMdd");
        }

        new NewsBootstrap(
                workingDirecory,
                new Supplier(
                        new SupplierID(supplier.getId()),
                        new SupplierCode(supplier.getCode()),
                        new SupplierName(supplier.getName())),
                from, to).
                run();
    }

    public void run() throws IOException, FeedException {
        switch (supplier.getId().getId()) {
            case 1:
                NewsApplication application = new NewsApplication();
                List<News> newsList = application.retrieve(workingFile);
                application.store(newsList);
                break;
            default:
                break;
        }
    }
}