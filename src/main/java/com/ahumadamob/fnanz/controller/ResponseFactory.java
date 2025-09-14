package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para construir respuestas estandarizadas de la API.
 */
public final class ResponseFactory {

    private ResponseFactory() {
    }

    /**
     * Crea una respuesta de éxito simple con estado 200.
     */
    public static <T> ApiResponseSuccessDto<T> ok(T data) {
        return ok(null, data);
    }

    /**
     * Crea una respuesta de éxito con mensaje personalizado.
     */
    public static <T> ApiResponseSuccessDto<T> ok(String message, T data) {
        return ApiResponseSuccessDto.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Crea una respuesta con estado 201 y encabezado Location generado
     * a partir del identificador del recurso recién creado.
     *
     * @param id   identificador del recurso creado
     * @param data cuerpo de la respuesta
     * @param <T>  tipo del cuerpo de respuesta
     * @return respuesta con estado 201 y encabezado Location
     */
    public static <T> ResponseEntity<ApiResponseSuccessDto<T>> created(Object id, String message, T data) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).body(ok(message, data));
    }

    public static <T> ResponseEntity<ApiResponseSuccessDto<T>> created(Object id, T data) {
        return created(id, null, data);
    }

    /**
     * Crea una respuesta paginada con encabezados X-Total-Count y Link.
     */
    public static <T> ResponseEntity<ApiResponseSuccessDto<List<T>>> page(Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        String link = createLinkHeader(page);
        if (!link.isEmpty()) {
            headers.add(HttpHeaders.LINK, link);
        }
        return new ResponseEntity<>(ok(page.getContent()), headers, HttpStatus.OK);
    }

    private static String createLinkHeader(Page<?> page) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        List<String> links = new ArrayList<>();
        if (page.hasPrevious()) {
            links.add(buildLink(builder, page.getNumber() - 1, page.getSize(), "prev"));
            links.add(buildLink(builder, 0, page.getSize(), "first"));
        }
        if (page.hasNext()) {
            links.add(buildLink(builder, page.getNumber() + 1, page.getSize(), "next"));
            links.add(buildLink(builder, page.getTotalPages() - 1, page.getSize(), "last"));
        }
        return String.join(", ", links);
    }

    private static String buildLink(UriComponentsBuilder builder, int page, int size, String rel) {
        String uri = builder.replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .toUriString();
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
