package io.github.vaporsea.vsindustry.contract;

import java.io.Serializable;
import java.util.List;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
public record Page<T>(int page, int totalPages, long totalElements, List<T> content) implements Serializable {

}
