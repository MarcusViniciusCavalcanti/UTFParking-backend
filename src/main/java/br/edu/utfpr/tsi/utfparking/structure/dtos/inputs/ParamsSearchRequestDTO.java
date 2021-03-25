package br.edu.utfpr.tsi.utfparking.structure.dtos.inputs;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Data
public class ParamsSearchRequestDTO {
    public static final String DEFAULT_SORT_DIRECT = "ASC";
    public static final String DEFAULT_SORT_PROPERTY = "name";
    public static final Boolean DEFAULT_ENABLED = Boolean.TRUE;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_SIZE_ELEMENT = 5;

    private int page;
    private int size;
    private String sortDirection;
    private String sort;
    private String name;
    private String profile;
    private String type;
    private Boolean enabled;

    public int getOrDefaultPage() {
        if (page <= 0) {
            return DEFAULT_PAGE_NUMBER;
        }

        return page;
    }

    public int getOrDefaultSize() {
        if (size <= 0) {
            return DEFAULT_SIZE_ELEMENT;
        }

        return size;
    }

    public String getOrDefaultSortDirection() {
        if (StringUtils.isEmpty(sortDirection)) {
            return DEFAULT_SORT_DIRECT;
        }

        return sortDirection;
    }

    public String getOrDefaultSort() {
        if (StringUtils.isEmpty(sort)) {
            return DEFAULT_SORT_PROPERTY;
        }

        return sort;
    }

    public Boolean getOrDefaultEnabled() {
        if (Objects.isNull(enabled)) {
            return DEFAULT_ENABLED;
        }

        return enabled;
    }
}
