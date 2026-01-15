package br.ind.powerx.gestaoOperacional.services.order.definition;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class XLSXColumn<T> extends Object{
    private String header;
    private Function<T, ?> valueExtractor;
}