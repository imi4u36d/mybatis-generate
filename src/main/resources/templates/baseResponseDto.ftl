package ${packageUrl};

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDto<T> implements Serializable {

    private Integer code;

    private T data;

    private String msg;
}
