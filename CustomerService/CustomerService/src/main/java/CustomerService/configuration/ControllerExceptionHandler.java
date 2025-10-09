package CustomerService.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /**
     * Xử lý RuntimeException cho Controller trả về view
     */
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        log.error("Controller runtime exception: ", ex);
        
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        modelAndView.addObject("errorMessage", "Có lỗi xảy ra, vui lòng thử lại sau");
        modelAndView.addObject("errorDetails", ex.getMessage());
        
        return modelAndView;
    }

    /**
     * Xử lý Exception chung cho Controller trả về view
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        log.error("Controller unexpected error: ", ex);
        
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        modelAndView.addObject("errorMessage", "Có lỗi xảy ra, vui lòng thử lại sau");
        modelAndView.addObject("errorDetails", ex.getMessage());
        
        return modelAndView;
    }
}
