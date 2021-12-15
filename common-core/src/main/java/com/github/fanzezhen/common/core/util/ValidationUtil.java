package com.github.fanzezhen.common.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Set;

/**
 * @author zezhen.fan
 */
@Slf4j
public class ValidationUtil {

    public static <T> void validate(T bean) {
        validate(bean, StrUtil.EMPTY, StrUtil.EMPTY);
    }

    public static <T> void validate(T bean, String startMsg, String endMsg) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (CollectionUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(startMsg + violations.iterator().next().getMessage() + endMsg, violations);
    }

    public static <T> Set<ConstraintViolation<T>> loadViolationSet(T bean) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(bean);
    }

    public static <T> void throwInValidate(T bean) {
        Set<ConstraintViolation<T>> violations = loadViolationSet(bean);
        if (CollectionUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(violations.iterator().next().getMessage(), violations);
    }

    public static void validateImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            ExceptionUtil.throwIfBlank(image);
        } catch (Throwable throwable) {
            log.warn("图片校验不通过！", throwable);
            throw new ValidationException("图片校验不通过！");
        }
    }

    public static boolean isImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                return true;
            }
        } catch (Throwable e) {
            throw new RuntimeException("图片格式不正确！");
        }
        return false;
    }

    public static void validateImage(MultipartFile imageMultipartFile) {
        try {
            BufferedImage image = ImageIO.read(imageMultipartFile.getInputStream());
            ExceptionUtil.throwIfBlank(image);
        } catch (Throwable throwable) {
            log.warn("图片校验不通过！", throwable);
            throw new ValidationException("图片校验不通过！");
        }
    }

    public static boolean isImage(MultipartFile imageMultipartFile) {
        try {
            BufferedImage image = ImageIO.read(imageMultipartFile.getInputStream());
            if (image != null) {
                return true;
            }
        } catch (Throwable e) {
            throw new RuntimeException("图片格式不正确！");
        }
        return false;
    }

    public static void validateImage(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            ExceptionUtil.throwIfBlank(image);
        } catch (Throwable throwable) {
            log.warn("图片校验不通过！", throwable);
            throw new ValidationException("图片校验不通过！");
        }
    }

    public static boolean isImage(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return true;
            }
        } catch (Throwable e) {
            throw new RuntimeException("图片格式不正确！");
        }
        return false;
    }
}
