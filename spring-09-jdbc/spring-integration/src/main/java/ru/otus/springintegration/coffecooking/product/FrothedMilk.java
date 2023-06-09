
package ru.otus.springintegration.coffecooking.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FrothedMilk extends AbstractProduct {

    @Override
    public String getName() {
        return "Frothed milk";
    }
}
