package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class ExistValidAspect {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Pointcut("@annotation(ru.practicum.shareit.util.ExistValid)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void isExist(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ExistValid annotation = method.getAnnotation(ExistValid.class);

        ModelType modelType = annotation.value();
        Object[] args = joinPoint.getArgs();
        String[] params = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        Long id = getFieldValue(annotation.idPropertyName(), args, params);

        switch (modelType) {
            case USER:
                userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", id)));
                break;
            case ITEM:
                itemRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", id)));
                break;
            case BOOKING:
                bookingRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", id)));
            case ITEM_REQUEST:
                itemRequestRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", id)));
                break;
        }
    }

    private Long getFieldValue(String fieldName, Object[] args, String[] params) {

        for (int i = 0; i < args.length; i++) {
            if (params[i].equals(fieldName)) {
                return (Long) args[i];
            }
        }

        for (Object arg : args) {
            ExpressionParser expressionParser = new SpelExpressionParser();
            Expression expression = expressionParser.parseExpression(fieldName);
            EvaluationContext context = new StandardEvaluationContext(arg);
            try {
                return (Long) expression.getValue(context);
            } catch (EvaluationException ignored) {
            }
        }
        throw new ValidationException(String.format(
                "Unable to find field with name %s.", fieldName));
    }
}
