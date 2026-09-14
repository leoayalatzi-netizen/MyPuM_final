const Router = {
    current: 'home',
    navigate(route) {
        this.current = route;
        console.log('Navigated to', route);
    }
};
