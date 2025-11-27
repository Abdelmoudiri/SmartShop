package com.SmartShop.SmartShop.util;

import com.SmartShop.SmartShop.entities.User;
import com.SmartShop.SmartShop.entities.enums.UserRole;
import com.SmartShop.SmartShop.exceptions.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class SecurityUtil {


    public static User getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            throw new BusinessException("Aucune session active");
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new BusinessException("Utilisateur non authentifié");
        }
        
        return currentUser;
    }


    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }


    public static UserRole getCurrentUserRole() {
        return getCurrentUser().getRole();
    }


    public static boolean isAdmin() {
        return getCurrentUserRole() == UserRole.ADMIN;
    }

    public static boolean isClient() {
        return getCurrentUserRole() == UserRole.CLIENT;
    }


    public static boolean hasAnyRole(UserRole... roles) {
        UserRole currentRole = getCurrentUserRole();
        for (UserRole role : roles) {
            if (currentRole == role) {
                return true;
            }
        }
        return false;
    }


    public static void requireRole(UserRole role) {
        if (getCurrentUserRole() != role) {
            throw new BusinessException("Accès refusé. Rôle requis : " + role);
        }
    }


    public static void requireAnyRole(UserRole... roles) {
        if (!hasAnyRole(roles)) {
            throw new BusinessException("Accès refusé. Rôles autorisés : " + String.join(", ", 
                java.util.Arrays.stream(roles).map(Enum::name).toArray(String[]::new)));
        }
    }


    public static boolean isAdminOrOwner(Long resourceOwnerId) {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == UserRole.ADMIN || 
               currentUser.getId().equals(resourceOwnerId);
    }


    public static void requireAdminOrOwner(Long resourceOwnerId) {
        if (!isAdminOrOwner(resourceOwnerId)) {
            throw new BusinessException("Accès refusé. Vous ne pouvez accéder qu'à vos propres données.");
        }
    }


    public static void requireAdmin() {
        requireRole(UserRole.ADMIN);
    }
}
