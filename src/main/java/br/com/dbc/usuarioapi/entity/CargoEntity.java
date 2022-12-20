package br.com.dbc.usuarioapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;


@Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity(name = "CARGO")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class CargoEntity implements GrantedAuthority {

        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CARGO_SEQ")
        @SequenceGenerator(name = "CARGO_SEQ", sequenceName = "seq_cargo", allocationSize = 1)
        @Id
        @Column(name = "ID_CARGO")
        private Integer idCargo;
        @Column(name = "NOME")
        private String nome ;

        @Column(name = "DESCRICAO")
        private String descricao ;

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "USUARIO_CARGO",
                joinColumns = @JoinColumn(name = "ID_CARGO"),
                inverseJoinColumns = @JoinColumn(name = "ID_USUARIO")
        )
        private Set<UsuarioEntity> usuarios;

        @Override
        public String getAuthority() {
            return nome;
        }
}
