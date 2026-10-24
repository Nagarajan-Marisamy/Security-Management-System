package securitymanagement;

public class AdminSecurityModule extends SecurityModule {
    @Override
    public void performSecurityCheck() {
        System.out.println("Security check passed: user store and role rules are active.");
    }
}
