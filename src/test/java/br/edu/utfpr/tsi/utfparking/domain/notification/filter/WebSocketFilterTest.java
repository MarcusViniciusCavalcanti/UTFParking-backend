package br.edu.utfpr.tsi.utfparking.domain.notification.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketFilterTest {

    public static final String ORIGIN = "origin";
    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Test
    void shouldAddOriginInResponse() throws ServletException, IOException {
        var webSocketFilter = new WebSocketFilter();

        when(httpServletRequest.getRequestURI()).thenReturn("/ws");
        when(httpServletRequest.getHeader("Origin")).thenReturn(ORIGIN);

        webSocketFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(httpServletResponse).setHeader("Access-Control-Allow-Credentials", "true");
        verify(httpServletResponse).setHeader("Access-Control-Max-Age", "3600");
        verify(httpServletResponse).setHeader("Access-Control-Allow-Origin", ORIGIN);
    }

    @Test
    void shouldNotAddHeaderInResponse() throws ServletException, IOException {
        var webSocketFilter = new WebSocketFilter();

        when(httpServletRequest.getRequestURI()).thenReturn("/otherUri");

        webSocketFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(httpServletResponse, times(0)).setHeader(anyString(), anyString());
    }
}
