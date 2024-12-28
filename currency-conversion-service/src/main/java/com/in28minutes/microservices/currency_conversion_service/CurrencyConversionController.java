package com.in28minutes.microservices.currency_conversion_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController
{
    @Autowired
    private CurrencyExchangeProxy proxy;

    @Autowired
    private Environment environment;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity)
    {

       //String port = environment.getProperty("local.server.port");

       HashMap<String,String> uriVariable = new HashMap<>();
       uriVariable.put("from",from);
       uriVariable.put("to",to);
      ResponseEntity<CurrencyConversion> responseEntity=  new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",CurrencyConversion.class,uriVariable);
      CurrencyConversion currencyConversion=responseEntity.getBody();

       return new CurrencyConversion(responseEntity.getBody().getId(), from,to,quantity,
               responseEntity.getBody().getConversionMultiple(), quantity.multiply(responseEntity.getBody().getConversionMultiple()),responseEntity.getBody().getEnvironment()+" Rest Template");
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity)
    {
        CurrencyConversion currencyConversion=proxy.retrieveExchangeValue(from,to);

        return new CurrencyConversion(currencyConversion.getId(), from,to,quantity,
                currencyConversion.getConversionMultiple(), quantity.multiply(currencyConversion.getConversionMultiple()),currencyConversion.getEnvironment()+" Feign");
    }
}
