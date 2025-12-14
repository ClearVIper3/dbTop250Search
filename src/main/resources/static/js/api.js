const api = {
    getMovies(page, size) {
        return axios.get(`http://localhost:9090/search/${page}/${size}`)
    },

    proxyImage(url) {
        return `http://localhost:9090/image-proxy?url=${encodeURIComponent(url)}`
    }
}