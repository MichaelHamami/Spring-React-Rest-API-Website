

export const logout = () => {


    localStorage.removeItem('user');
    localStorage.removeItem('username');
    window.location = '/';

}